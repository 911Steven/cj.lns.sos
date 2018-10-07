package cj.lns.chip.sos.website.sws.component.market;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "/market/appFair.html")
public class AppFairComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		String url = "/portal/market/appFair.service";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		m.site().out().flow(frame, circuit);
		String json = new String(circuit.content().readFully());
		List<AppSO> apps = new Gson().fromJson(json,
				new TypeToken<List<AppSO>>() {
				}.getType());
		Document doc = m.context().html("/market/appFair.html",
				m.site().contextPath(), "utf-8");
		String name = (String) circuit.attribute("category-name");
		String desc = (String) circuit.attribute("category-desc");
		String icon = (String) circuit.attribute("category-icon");
		doc.body().prepend(String.format(
				"<nav imgsrc='%s' title='%s' desc='%s'/>", icon, name, desc));

		Element list = doc.select(".app-list>.content-panel>.list").first();
		Element item = list.select(".item").first().clone();
		list.empty();

		IServicewsContext ctx = IServicewsContext.context(frame);
		List<AppSO> mounted = sws.getMountedApp(ctx.swsid());
		List<String> appids = new ArrayList<>();
		for (AppSO app : mounted) {
			appids.add(app.getId());
		}
		mounted.clear();
		for (AppSO app : apps) {
			if (!app.getSosPermissions().contains("visible")) {
				continue;
			}
			String thePlatform = app.getPlatform();
			if (!StringUtil.isEmpty(thePlatform) && !"*".equals(thePlatform)
					&& !thePlatform.equals(ctx.prop("portal.platform"))) {
				continue;
			}
			Element li = item.clone();
			li.attr("appId", app.getId());
			li.attr("provider", app.getProvider());
			// li.attr("providerId", app.getProviderId());
			li.attr("category", app.getCategory());
			boolean isMountable = app.getSosPermissions().contains("mountable")
					? true : false;
			li.attr("mountable", String.valueOf(isMountable));
			if (appids.contains(app.getId())) {
				li.select("input[name=selectedApp]").attr("checked", "checked");
			}
			li.attr("title", app.getName());
			li.select("[appimg]").attr("src", app.getIcon());
			li.select("[appname]").html(app.getName());
			list.appendChild(li);
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}

}
