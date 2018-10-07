package cj.lns.chip.sos.website.market.app.controlpanel.provider;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.IWebsiteConstants;
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

@CjService(name = "controlPanelSoProvider")
public class ControlPanelSoProvider implements ISecurityObjectProvider {

	private IAclFinder finder;
	AppSO root;
	private IAclSetting setting;
	public IAclFinder finder() {
		return finder;
	}
	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder,IAclSetting setting) {
		this.finder = finder;
		this.setting=setting;
		root = new AppSO();
		root.setPhyId(BigInteger.valueOf(IWebsiteConstants.FIXED_APP_CONTROLPANEL));
		root.setProvider(hostId);
		root.setSort(-100);
		root.setId(ISurfacePosition.POSITION_WIN_CONTROLPANEL);
		root.setName("控制面板");
		root.setDesc("控制面板");
		root.setCategory(this.category());
		root.setPosition(ISurfacePosition.POSITION_TOOLBAR_WIN_APP);
		root.setCommand("./controlPanelApp/");
		root.setIcon("img/control.svg");
//		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
//		InputStream in = m.chip().info().getIconStream();
//		if (in != null) {
//			try {
//				m.context().writeFile(in, "/img/app.png");
//				String url = String.format("./controlPanelApp/img/app.png");
//				root.setIcon(url);
//			} catch (CircuitException e) {
//				e.printStackTrace();
//			}
//
//		}
		root.setTarget("_self");
		return null;
	}
	public IAclSetting setting() {
		return setting;
	}
	@Override
	public String category() {
		return "application";
	}

	@Override
	public AppSO root() {

		return root;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if (Long.valueOf(valueId).equals(root.getPhyId().longValue())) {
			CjSecurityObject so = root.getClass()
					.getAnnotation(CjSecurityObject.class);
			if (resourceId.equals(so.resourceDefineId())) {
				return root;
			}
		}
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("findAppByPhyId /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("phyId",valueId);
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
			AppSO app=new Gson().fromJson(json, AppSO.class);
			if (ctx.isOwner()) {// 如果 是持有人则取出全部应用，如果是非持有人则以ACL过滤应用或不列出
				return app;
			}
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
		return null;
	}

	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		if(!String.valueOf(IWebsiteConstants.FIXED_APP_CONTROLPANEL).equals(valueId)){//由于应用只有两层，一层是宿主，一层是应用，因此
			return new ArrayList<>();
		}
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("getMountedApps /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("position",ISurfacePosition.POSITION_WIN_CONTROLPANEL);
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
			List<AppSO> list = new Gson().fromJson(json,
					new TypeToken<List<AppSO>>() {
					}.getType());
			if (ctx.isOwner()) {// 如果 是持有人则取出全部应用，如果是非持有人则以ACL过滤应用或不列出
				return (List<?>) list;
			}
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
		return new ArrayList<>();
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		if (ctx.isOwner())
			return true;
		return false;
	}
}
