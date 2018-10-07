package cj.lns.chip.sos.website.sws.component.popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.IWebsiteConstants;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.so.ToolSO;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.IWebSiteConstans;
import cj.ultimate.util.StringUtil;

/*
 * 在进入视窗之前验证：
 * · 有没有登录服务操作系统
 * · 视窗是不是开放视窗，即开放级别
 *   是开放给sos，还是开放给大众。如果是前者，则来访者必须先登录sos，否则不能使用。如果是开放给大众，不论是否登录sos都可访问视窗
 * · 是不是视窗的持有人来访
 *   如果是则全权访问
 * · 是联系人来访，调出联系人认证界面
 *   联系人角色、归属组等，采用基于角色的权限认证。如果联系人属于管理员角色，则和持有人一样拥有全权。
 *   
 *   -> sos   ->   sws
 *     统一认证    视窗认证
 *     语义为：谁来访问视窗，在同一会话中切换多视窗(任何视窗），在时间切片上最多只存在一个视窗
 */
@CjService(name = "/components/appPopup.html")
public class AppPopupComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;
	@CjServiceRef(refByName = "AppWinToolSoProvider")
	ISecurityObjectProvider appProvider;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);
		List<?> list = appProvider.getChilds("tool",
				ISurfacePosition.POSITION_TOOLBAR_WIN_APP,
				ISubject.subject(frame), sws);
		List<Object> mainApps = new ArrayList<>();
		List<Object> removed = new ArrayList<>();
		boolean useComputer = "computer".equals(sws.prop("portal.platform"));
		for (Object o : list) {
			AppSO app = (AppSO) o;
			int pid = app.getPhyId().intValue();
			if (pid < 0) {// 为固定应用
				if (useComputer) {
					if (sws.level() <= 1) {
						if (IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL1 != pid
								&& IWebsiteConstants.FIXED_APP_CONTROLPANEL != pid) {
							removed.add(app);
							continue;
						}
					} else {
						if (IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL2 != pid
								&& IWebsiteConstants.FIXED_APP_CONTROLPANEL != pid) {
							removed.add(app);
							continue;
						}
					}

				} else {
					if (IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL1 == pid
							|| IWebsiteConstants.FIXED_APP_COMPUTER_LEVEL2 == pid) {
						removed.add(app);
						continue;
					}
				}
				mainApps.add(app);
				continue;
			}
			String hidden = app.getHidden();
			if (!StringUtil.isEmpty(hidden) && hidden
					.contains(ISurfacePosition.POSITION_TOOLBAR_WIN_APP)) {
				removed.add(app);
			}
		}
		list.removeAll(removed);// 移除要被隐藏的应用
		Collections.sort(mainApps, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				AppSO a1 = (AppSO) o1;
				AppSO a2 = (AppSO) o2;
				return a1.getSort() > a2.getSort() ? -1 : 1;
			}

		});
		list.removeAll(mainApps);
		Document doc = m.context().html("/components/app-popup.html",
				m.site().contextPath(), "utf-8");
		Element mainul = doc.select(".app-popup>.right-region>.a-t-box")
				.first();
		Element mainli = mainul.select("li").first().clone();
		mainul.empty();
		renderApp(mainApps, mainul, mainli, m, frame);

		Element appul = doc.select(".app-popup>.app-region>.a-t-box").first();
		Element appli = appul.select("li").first().clone();
		appul.empty();
		renderApp(list, appul, appli, m, frame);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void renderApp(List<?> list, Element ul, Element li,
			IServiceosWebsiteModule m, Frame frame) {
		for (Object o : list) {
			AppSO app = (AppSO) o;
			li.attr("appId", app.getId());
			li.attr("resourceId", "application");
			ToolSO root = (ToolSO) appProvider.root();
			li.attr("tool", root.getId());
			li.attr("cjopen", app.getPhyId().toString());
			if (!StringUtil.isEmpty(app.getDesc()))
				li.attr("title", app.getDesc());
			if (!StringUtil.isEmpty(app.getIcon())) {
				li.select("img").attr("src", app.getIcon());
			}
			li.select("span").html(app.getName());
			ul.appendChild(li);
			li = li.clone();
		}
	}

}
