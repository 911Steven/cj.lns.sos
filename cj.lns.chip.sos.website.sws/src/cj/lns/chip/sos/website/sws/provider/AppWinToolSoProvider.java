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
import cj.lns.chip.sos.sws.security.ISecuritySite;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.AppMenu;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.IWebsiteConstants;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.so.ToolSO;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "AppWinToolSoProvider")
public class AppWinToolSoProvider implements ISecurityObjectProvider {
	private Object sitehome;
	private Object controlPanel;
	private Object cyberport;// 信息港主页
	private Object computerlevel2;
	private Object computerlevel1;
	private ISecurityObjectProvider sitehomeProvider;
	public class AppSoProviderHandler implements ISoProviderHandler {

		

		@Override
		public void onCenterReady(ISecuritySite site) {
			Map<String, ISecurityObjectProvider> map = site
					.resourceProviders("application");
			ISecurityObjectProvider sp = map.get(
					String.valueOf(IWebsiteConstants.FIXED_APP_CONTROLPANEL));
			if (sp != null) {
				controlPanel = sp.root();
			}
			sp = map.get(String.valueOf(IWebsiteConstants.FIXED_APP_CYBERPORT));
			if (sp != null) {
				cyberport = sp.root();
			}
			sp = map.get(String.valueOf(IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL2));
			if (sp != null) {
				computerlevel2 = sp.root();
			}
			sp = map.get(String.valueOf(IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL1));
			if (sp != null) {
				computerlevel1 = sp.root();
			}
			sp = map.get(String.valueOf(IWebsiteConstants.FIXED_APP_HOME));
			if (sp != null) {
				sitehome = sp.root();
				sitehomeProvider=sp;
			}
		}

	}

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
		frame.parameter("position", ISurfacePosition.POSITION_TOOLBAR_WIN_APP);
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
			AppSO app = new Gson().fromJson(json, AppSO.class);
			if (ctx.isOwner()) {// 如果 是持有人则取出全部应用，如果是非持有人则以ACL过滤应用或不列出
				return app;
			}
			if("swssite".equals(app.getProvider())){
				filterSwssiteMenus(app,ctx);
			}
			return app;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	private void filterSwssiteMenus(AppSO app, IServicewsContext ctx) throws CircuitException {
		List<Acl> acls = finder.getAcls("application", "visible", ctx);
		List<?> apps=getMountedApp(ISurfacePosition.POSITION_TOOLBAR_WIN_APP, ctx);
		List<String> oklist=new ArrayList<>();
		for (Object o : apps) {
			AppSO so = (AppSO) o;
			for (Acl acl : acls) {
				if (acl.getValueId().equals(so.getPhyId().toString())) {
					if (acl.containsRole("publicToNet")) {
						oklist.add(so.getName());
						continue;
					}
					if (acl.containsRole("publicToMember")) {
						oklist.add(so.getName());
						continue;
					}
					if(acl.getAllows().length>0){
						oklist.add(so.getName());
					}
				}
			}
		}
		List<AppMenu> mlist=new ArrayList<>();
		List<AppMenu>old=app.getMenus();
		for(AppMenu m:old){
			if(oklist.contains(m.getName())){
				mlist.add(m);
			}
		}
		old.clear();
		old.addAll(mlist);
	}

	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) throws CircuitException {
		if ("tool".equals(resourceId)) {
			List<Object> list = new ArrayList<>();
			if (ctx.isOwner()) {
				if (sitehome != null)
					list.add(sitehome);
				if (cyberport != null)//即动态主页
					list.add(cyberport);
				if (controlPanel != null)
					list.add(controlPanel);
				if (computerlevel2 != null)
					list.add(computerlevel2);
				if (computerlevel1 != null)
				list.add(computerlevel1);
				List<?> r = getMountedApp(valueId, ctx);
				list.addAll(r);
				return list;
			}else{
				List<Acl> acllist = finder.getAcls("application", "visible", ctx);
				if(acllist.isEmpty()){
					return list;
				}
				List<?> r = getMountedApp(valueId, ctx);
				Map<String,Object> map=new HashMap<>();
				for(Object o:r){
					ISecurityObject so=ISecurityObject.securityObject(o);
					map.put(so.valueId(), o);
				}
				for(Acl acl:acllist){
					if (sitehome != null){
						ISecurityObject so=ISecurityObject.securityObject(sitehome);
						if(acl.getValueId().equals(so.valueId())){
							list.add(sitehome);
							continue;
						}
					}
					if (cyberport != null){
						ISecurityObject so=ISecurityObject.securityObject(cyberport);
						if(acl.getValueId().equals(so.valueId())){
							list.add(cyberport);
							continue;
						}
					}	
					if (computerlevel2 != null){
						ISecurityObject so=ISecurityObject.securityObject(computerlevel2);
						if(acl.getValueId().equals(so.valueId())){
							list.add(computerlevel2);
							continue;
						}
					}	
					if (computerlevel1 != null){
						ISecurityObject so=ISecurityObject.securityObject(computerlevel1);
						if(acl.getValueId().equals(so.valueId())){
							list.add(computerlevel1);
							continue;
						}
					}	
					if (controlPanel != null){
						ISecurityObject so=ISecurityObject.securityObject(controlPanel);
						if(acl.getValueId().equals(so.valueId())){
							list.add(controlPanel);
							continue;
						}
					}	
					if(map.containsKey(acl.getValueId())){
						Object o=map.get(acl.getValueId());
						if (subject.containsRole("guestUsers")) {
							if (acl.isAllowSubject("role", "publicToNet")) {
								list.add(o);
							}
						} else {
							if (acl.isAllowSubject("role", "publicToMember")) {
								if (map.containsKey(acl.getValueId()))
									list.add(map.get(acl.getValueId()));
								continue;
							} else {
								if (map.containsKey(acl.getValueId())) {
									list.add(map.get(acl.getValueId()));
								}
								continue;
							}
						}
					}
				}
				
				return list;
			}
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
		if ("application".equals(resourceId)) {
			if (String.valueOf(IWebsiteConstants.FIXED_APP_CYBERPORT)
					.equals(valueId)) {
				return cyberport;
			}
			if (String.valueOf(IWebsiteConstants.FIXED_APP_CONTROLPANEL)
					.equals(valueId)) {
				return controlPanel;
			}
			if (String.valueOf(IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL2)
					.equals(valueId)) {
				return computerlevel2;
			}
			if (String.valueOf(IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL1)
					.equals(valueId)) {
				return computerlevel1;
			}
			if (String.valueOf(IWebsiteConstants.FIXED_APP_HOME)
					.equals(valueId)) {
				return sitehome;
			}
			AppSO so= (AppSO)findApp(valueId, ctx);
			if(so==null)return null;
			if("swssite".equals(so.getProvider())){
				return sitehomeProvider.find(resourceId, valueId, subject, ctx);
			}
			return so;
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
		root.setDescription("应用列表窗口工具");
		root.setIcon("./startMenu/img/iconfont-yingyong.svg");
		root.setName("应用");
		root.setCommand("./servicews/components/appPopup.html");
		root.setId(ISurfacePosition.POSITION_TOOLBAR_WIN_APP);
		root.setSort(2);
		this.finder = finder;
		return new AppSoProviderHandler();
	}

}
