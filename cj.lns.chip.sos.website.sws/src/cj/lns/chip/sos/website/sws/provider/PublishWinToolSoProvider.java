package cj.lns.chip.sos.website.sws.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.so.ToolSO;
import cj.lns.chip.sos.website.sws.so.PublishSO;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "PublishWinToolSoProvider")
public class PublishWinToolSoProvider implements ISecurityObjectProvider {


	private ToolSO root;
	private IAclFinder finder;

	@Override
	public String category() {
		return "position";
	}

	@Override
	public ToolSO root() {
		return root;
	}
	private List<?> getMountedApp(String position, IServicewsContext ctx) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("getMountedApps /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("position",position);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		try {
			m.site().out().flow(frame, circuit);
			byte[] b = circuit.content().readFully();
			Frame back = new Frame(b);
			if (!"200".equals(back.head("status"))) {
				throw new EcmException("远程服务器出现错误：" + back.head("status") + " "
						+ back.head("message"));
			}
			String json = new String(back.content().readFully());
			List<PublishSO> list = new Gson().fromJson(json,
					new TypeToken<List<PublishSO>>() {
					}.getType());
			if (ctx.isOwner()) {// 如果 是持有人则取出全部应用，如果是非持有人则以ACL过滤应用或不列出
				return (List<?>) list;
			}
			return list;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	private Object findApp(String valueId, IServicewsContext ctx) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("findAppByPhyId /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("phyId", valueId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		try {
			m.site().out().flow(frame, circuit);
			byte[] b = circuit.content().readFully();
			Frame back = new Frame(b);
			if (!"200".equals(back.head("status"))) {
				throw new EcmException("远程服务器出现错误：" + back.head("status") + " "
						+ back.head("message"));
			}
			String json = new String(back.content().readFully());
			PublishSO app = new Gson().fromJson(json, PublishSO.class);
			if (ctx.isOwner()) {// 如果 是持有人则取出全部应用，如果是非持有人则以ACL过滤应用或不列出
				return app;
			}
			return app;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}
	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) throws CircuitException {
		if ("tool".equals(resourceId)) {
			List<Object> r =(List<Object>) getMountedApp(valueId, ctx);
			if(ctx.isOwner()){
				return r;
			}
			List<Acl> acllist = finder.getAcls("publish", "visible", ctx);
			if(acllist.isEmpty()){
				return new ArrayList<>();
			}
			Map<String,Object> map=new HashMap<>();
			for(Object o:r){
				ISecurityObject so=ISecurityObject.securityObject(o);
				map.put(so.valueId(), o);
			}
			r.clear();
			for(Acl acl:acllist){
				if(map.containsKey(acl.getValueId())){
					Object o=map.get(acl.getValueId());
					if (subject.containsRole("guestUsers")) {
						if (acl.isAllowSubject("role", "publicToNet")) {
							r.add(o);
						}
					} else {
						if (acl.isAllowSubject("role", "publicToMember")) {
							if (map.containsKey(acl.getValueId()))
								r.add(map.get(acl.getValueId()));
							continue;
						} else {
							if (map.containsKey(acl.getValueId())) {
								r.add(map.get(acl.getValueId()));
							}
							continue;
						}
					}
					
				}
			}
			return r;
		}
		return null;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if ("tool".equals(resourceId)) {
			if (valueId.equals(root.getId())) {
				return root;
			}
			return null;
		}
		if ("publish".equals(resourceId)) {
			return findApp(valueId, ctx);
		}
		return null;
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder,
			IAclSetting setting) {
		root = new ToolSO();
		root.setDescription("发布工具");
		root.setIcon("./startMenu/img/iconfont-jiahao.svg");
		root.setName("发布");
		root.setId(ISurfacePosition.POSITION_TOOLBAR_WIN_PUBLISH);
		root.setCommand("./servicews/components/publishPopup.html");
		root.setSort(1);
		this.finder = finder;
		return null;
	}

}
