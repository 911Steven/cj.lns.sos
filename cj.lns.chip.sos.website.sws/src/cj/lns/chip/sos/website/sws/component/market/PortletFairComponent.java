package cj.lns.chip.sos.website.sws.component.market;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.website.PortletSO;
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

@CjService(name = "/market/portletFair.html")
public class PortletFairComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		String url="/portal/market/portletFair.service";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		m.site().out().flow(frame, circuit);
		String json = new String(circuit.content().readFully());
		List<PortletSO> lets = new Gson().fromJson(json, new TypeToken<List<PortletSO>>() {
		}.getType());
		Document doc = m.context().html("/market/portletFair.html",
				m.site().contextPath(), "utf-8");
		String name = (String) circuit.attribute("category-name");
		String desc = (String) circuit.attribute("category-desc");
		String icon = (String) circuit.attribute("category-icon");
		doc.body().prepend(String.format(
				"<nav imgsrc='%s' title='%s' desc='%s'/>", icon, name, desc));

		Element list = doc.select(".portlet-list>.content-panel>.list").first();
		Element item = list.select(".item").first().clone();
		list.empty();
		
		IServicewsContext ctx=IServicewsContext.context(frame);
		List<PortletSO> mounted = sws.getMountedPortlet(ctx.swsid());
		List<String> letids=new ArrayList<>();
		for(PortletSO let:mounted){
			letids.add(let.getId());
		}
		mounted.clear();
		for (PortletSO let : lets) {
			if (!let.getSosPermissions().contains("visible")) {
				continue;
			}
			String thePlatform = let.getPlatform();
			if (!StringUtil.isEmpty(thePlatform) && !"*".equals(thePlatform)
					&& !thePlatform.equals(ctx.prop("portal.platform"))) {
				continue;
			}
			Element li = item.clone();
			li.attr("portletId", let.getId());
			li.attr("provider", let.getProvider());
//			li.attr("providerId", let.getProviderId());
			li.attr("category", let.getCategory());
			boolean isMountable = let.getSosPermissions().contains("mountable")
					? true : false;
			li.attr("mountable", String.valueOf(isMountable));
			if (letids.contains(let.getId())) {
				li.select("input[name=selectedPortlet]").attr("checked", "checked");
			}
			li.attr("title", let.getName());
			li.select("[portletimg]").attr("src", let.getIcon());
			li.select("[portletname]").html(let.getName());
			list.appendChild(li);
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}

}
