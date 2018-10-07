package cj.lns.chip.sos.website.market.app.cyberport.provider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IDocument;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.AppMenu;
import cj.lns.chip.sos.website.AppPortal;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppMenuLoader;
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
import cj.ultimate.util.StringUtil;

@CjService(name = "computerLevel1SecurityObjectProvider")
public class ComputerLeve1SoProvider implements ISecurityObjectProvider {
	private AppSO root;
	private IAppMenuLoader loader;
	private IAclFinder finder;

	@Override
	public String category() {
		return "application";
	}

	@Override
	public AppSO root() {
		return (AppSO) root;
	}
	private List<AppSO> getMountedApps(IServicewsContext ctx,String providers){
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("getMountedApps2 /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("providers", providers);
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
			return list;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}
	private List<AppSO> getMountedApps(IServicewsContext ctx) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame("getMountedApps /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("provider", m.chip().info().getId());
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
			return list;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	// 不同的视窗不同的主页模板
	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		if (!String.valueOf(root.getPhyId()).equals(valueId)) {
			return null;
		}
		List<?> apps = null;
		if(ctx.level()==1){//如果是基础视窗，则取出所有应用
			apps = getMountedApps(ctx,"cyberportApp,swssite");
		}else{
			apps = getMountedApps(ctx);
		}
		if (ctx.isOwner()) {
			return apps;
		}
		if (apps == null)
			return null;
		try {
			List<Object> list = new ArrayList<>();
			// home
			// 模块的另一部分授权（即站点的二级菜单对应的应用的权限过滤在AppWinToolSoProvider.filterSwssiteMenus()方法中实现
			// 看filterSwssiteMenus方法即知：二级应用菜单的定义，其名称一定要一致。
			List<Acl> acls = finder.getAcls("application", "visible", ctx);
			for (Object o : apps) {
				AppSO so = (AppSO) o;
				for (Acl acl : acls) {
					if (acl.getValueId().equals(so.getPhyId().toString())) {
						if (acl.containsRole("publicToNet")) {
							list.add(o);
							continue;
						}
						if (acl.containsRole("publicToMember")) {
							list.add(o);
							continue;
						}
						if (acl.getAllows().length > 0) {
							list.add(o);
						}
					}
				}
			}
			return list;
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	private AppSO findApp(String valueId, ISubject subject,
			IServicewsContext ctx) {
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
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
		return null;
	}

	// 不同的视窗不同的主页模板
	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if (valueId.equals(String.valueOf(root.getPhyId()))) {
			return root;
		}

		AppSO so = findApp(valueId, subject, ctx);
		// 为产品添加自定义菜单，可让用户定义自己的产品
		fillUserAppMenus(so, ctx, subject);
		return so;
	}

	private void fillUserAppMenus(AppSO so, IServicewsContext ctx,
			ISubject subject) {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		INetDisk disk = m.site().diskOwner(ctx.owner());
		List<AppMenu> menus = new ArrayList<>();
		if (disk.existsCube(ctx.swsid())) {
			ICube cube = disk.cube(ctx.swsid());
			String cjql = "select {'tuple':'*'} from tuple app.menus ?(clazz) where {'tuple.appid':'?(appid)'}";
			IQuery<AppMenu> q = cube.createQuery(cjql);
			q.setParameter("clazz", AppMenu.class.getName());
			q.setParameter("appid", so.getId());
			List<IDocument<AppMenu>> list = q.getResultList();
			for (IDocument<AppMenu> doc : list) {
				menus.add(doc.tuple());
			}
		}
		if (menus.isEmpty()) {
			return;
		}
		menus.addAll(so.getMenus());
		so.getMenus().clear();
		so.getMenus().addAll(menus);
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		if (ctx.isOwner())
			return true;
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder,
			IAclSetting setting) {
		// 信息港的作用就是进入港，因此它就是自己与别人的动态交互处。因此可命名为视窗的动态主页
		// 信息港是全领域应用，应用于所有平台
		this.finder = finder;
		this.loader = new AppMenuLoader();
		root = new AppSO(loader);
		root.setCategory(category());
		root.setDesc("云计算开发平台首页");
		root.setName("主页·云计算");
		root.setCommand("./cyberportApp/computer/level1/index.html");
		root.setId("site.computer.leve1.home");
		root.setPhyId(
				BigInteger.valueOf(IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL1));
		root.setPosition(ISurfacePosition.POSITION_TOOLBAR_WIN_APP);
		root.setSort(-10);
		root.setProvider(hostId);
		root.setIcon("img/guangjiewang.svg");
		AppPortal portal = new AppPortal();
//		portal.setLeft("./cyberportApp/left.html");
//		portal.setRight("./cyberportApp/computer/level1/right.html");
		root.setPortal(portal);
		return null;
	}

	class AppMenuLoader implements IAppMenuLoader {

		@Override
		public List<AppMenu> load(ISecurityObject so, ISubject subject,
				IServicewsContext ctx) {
			List<?> apps = getChilds(so.resourceId(), so.valueId(), subject,
					ctx);
			List<AppMenu> list = new ArrayList<>();
			if (apps == null) {
				return list;
			}
			for (Object o : apps) {
				AppSO app = (AppSO) o;
				String hidden=app.getHidden();
				if(!StringUtil.isEmpty(hidden)&&hidden.contains(ISurfacePosition.POSITION_WIN_MENU)){
					continue;
				}
				AppMenu menu = new AppMenu();
				// 如果按应用打开，则格式为：app://appPhyId
				// 如果只是打开一个页面，则直接写作：url
				menu.setCmd(
						String.format("app://%s", app.getPhyId().toString()));
				menu.setName(app.getName());
				menu.setIcon(app.getIcon());
				menu.setId(app.getId());
				list.add(menu);
			}
			return list;
		}
	}
}
