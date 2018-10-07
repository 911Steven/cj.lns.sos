package cj.lns.chip.sos.website.sws.component.market;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

@CjService(name = "/market/appSettingPanel.html")
public class AppSettingPanelComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc=m.context().html("/market/appSettingPanel.html", m.site().contextPath(), "utf-8");
		String url="/portal/market/getAppFairAllCategory.service";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		m.site().out().flow(frame, circuit);
		String json=new String(circuit.content().readFully());
		JsonElement je=new Gson().fromJson(json, JsonElement.class);
		JsonArray categories=je.getAsJsonArray();
		Elements es=doc.select(".app-setting>.content-panel>.list");
		Element list=es.first();
		Elements templates=list.select(".item");
		if(templates.isEmpty()){
			throw new CircuitException("503", "页面appSettingPanel.html 中缺少列表模板");
		}
		Element li=templates.first().clone();
		list.empty();
		for(JsonElement jeitem:categories){
			JsonObject jo=jeitem.getAsJsonObject();
			String id=ElementGet.getJsonProp(jo.get("categoryId"));
			String name=ElementGet.getJsonProp(jo.get("categoryName"));
//			String desc=ElementGet.getJsonProp(jo.get("categoryDesc"));
			Element newli=li.clone();
			newli.attr("openContent",String.format("./servicews/market/appFair.html?category=%s", id));
			newli.select("div.main-panel>div").html(name);
			
			list.appendChild(newli);
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}


}
