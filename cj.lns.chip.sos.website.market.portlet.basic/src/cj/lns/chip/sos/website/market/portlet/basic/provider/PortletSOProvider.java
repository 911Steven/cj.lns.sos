package cj.lns.chip.sos.website.market.portlet.basic.provider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "portletSecurityObjectProvider")
public class PortletSOProvider implements ISecurityObjectProvider {

	private PortletSO root;
	private IAclFinder finder;

	@Override
	public String category() {
		return "portlet";
	}

	@Override
	public PortletSO root() {
		return root;
	}

	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		if ("column".equals(resourceId)) {
			List<PortletSO> lets = getMountedPortlets("position", valueId,
					subject, ctx);
			if (lets.isEmpty())
				return lets;
			if (ctx.isOwner())
				return lets;// 全权
			return filterByAcl(resourceId, lets, subject, ctx);
		}
		if ("portlet".equals(resourceId)
				&& root.getPhyId().toString().equals(valueId)) {
			List<PortletSO> lets = getMountedPortlets("parent", valueId, subject,
					ctx);
			if (lets.isEmpty())
				return lets;
			if (ctx.isOwner())
				return lets;// 全权
			return filterByAcl(resourceId, lets, subject, ctx);
		}
		return null;
	}

	private List<?> filterByAcl(String resourceId, List<PortletSO> lets,
			ISubject subject, IServicewsContext ctx) {
		try {
			List<Acl> acls = finder.getAcls("portlet", "visible", ctx);
			Map<String, Acl> map = new HashMap<>();
			for (Acl a : acls) {
				map.put(a.getValueId(), a);
			}
			List<Object> ret = new ArrayList<>();
			for (PortletSO so : lets) {
				String phyId = so.getPhyId().toString();
				if (map.containsKey(phyId)) {
					ret.add(so);
				}
			}
			return ret;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if (!ctx.isOwner())
			return null;
		if (valueId.equals(root.getPhyId().toString())) {
			return root;
		}
		return findPortlet(valueId, subject, ctx);
	}

	private PortletSO findPortlet(String valueId, ISubject subject,
			IServicewsContext ctx) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("findPortlet /sws/market/portlet/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("phyId", valueId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		try {
			m.site().out().flow(frame, circuit);
			Frame back = new Frame(circuit.content().readFully());
			if ("404".equals(back.head("status"))) {
				return null;
			}
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						String.format("在远程服务器上出错。原因：%s %s", back.head("status"),
								back.head("message")));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json, PortletSO.class);
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	private List<PortletSO> getMountedPortlets(String filterCondision,
			String valueId, ISubject subject, IServicewsContext ctx) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame(
				"getMountedPortlets /sws/market/portlet/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		if ("position".equals(filterCondision)) {
			frame.parameter("position", valueId);
		}
//		if ("parentPhyId".equals(filterCondision)) {//由于数据库存的栏目必定是此提供器的子栏目，因此取出全部即是子，故注释掉。如果别的栏目模块也定义了自己的提供器，那它自己创建 自己的栏目表，爱存哪存哪。
//			frame.parameter("parent", valueId);
//		}
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		try {
			m.site().out().flow(frame, circuit);
			Frame back = new Frame(circuit.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						String.format("在远程服务器上出错。原因：%s %s", back.head("status"),
								back.head("message")));
			}
			String json = new String(back.content().readFully());
			List<PortletSO> lets = new Gson().fromJson(json,
					new TypeToken<List<PortletSO>>() {
					}.getType());
			return lets;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		if (ctx.isOwner())
			return true;
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder, IAclSetting setting) {
		root = new PortletSO();
		root.setCategory(category());
		root.setDescription("管理栏目对来访者的可见性");
		root.setIcon("img/portlet.svg");
		root.setName("栏目");
		root.setId("-1");
		root.setPhyId(new BigInteger("-1"));
		root.setPosition(ISurfacePosition.POSITION_DESKTOP_LEFT);
		root.setProvider(hostId);
		this.finder = finder;
		return null;
	}

}
