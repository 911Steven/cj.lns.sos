package cj.lns.chip.sos.website.sws.component.render;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.logging.ILogging;
import cj.studio.ecm.net.web.HttpCircuit;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "/portletRender.html")
public class PortletRenderComponent implements IComponent {
	ILogging log;
	public PortletRenderComponent() {
		log = CJSystem.current().environment().logging();
	}
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		String usage = frame.parameter("region.usage");
		List<PortletSO> lets = getPortlets(frame, circuit, usage, m);
		IPin out = m.site().out();
		Document letTemplate=Jsoup.parse(new String(frame.content().readFully()));
		Elements es=letTemplate.select(".portlet");
		if(es.isEmpty())return;
		Element lete=es.first().clone();
		
		for (PortletSO so : lets) {
			String url=so.getContentUrl();
			if (frame.containsQueryString()) {
				url = String.format("%s?%s", url, frame.queryString());
			}
			frame.url(url);
			frame.content().writeBytes(new Gson().toJson(so).getBytes());
			HttpCircuit c=new HttpCircuit(circuit.toString());
			c.attribute("http.root",circuit.attribute("http.root"));
			try {
				out.flow(frame, c);
				String html=new String(c.content().readFully());
				Document letcnt=Jsoup.parse(html);
				frame.content().clear();
				if("none".equals(so.getUseTemplate())){
					if(letcnt.select(".portlet").size()<1){
						log.error(getClass(),
								String.format("渲染栏目[id:%s,name:%s]出错。原因：栏目设置为useTemplate=none，却未定义div class='portlet'标签", so.getId(),
										so.getName()));
						continue;
					}
					letcnt.body().prepend(letcnt.head().html());
					circuit.content().writeBytes(letcnt.body().html().getBytes());
					continue;
				}
				lete=lete.clone();
				Element titlebar=lete.select(".titleBar").first();
				Element cnt=lete.select(".content").first();
				titlebar.select("a").html(so.getName());
				titlebar.attr("id",so.getPhyId().toString());
				cnt.empty();
				cnt.html(letcnt.body().html());
				circuit.content().writeBytes(lete.outerHtml().getBytes());
			} catch (Exception e) {
				log.error(getClass(),
						String.format("渲染栏目[id:%s,name:%s]出错。原因：%s", so.getId(),
								so.getName(), e.getMessage()));
				log.error(getClass(),e);
			}finally{
				c.dispose();
			}
		}
	}


	private List<PortletSO> getPortlets(Frame frame, Circuit circuit,
			String usage, IServiceosWebsiteModule m) throws CircuitException {
		String position = "";
		switch (usage) {
		case "left":
			position = ISurfacePosition.POSITION_DESKTOP_LEFT;
			break;
		case "right":
			position = ISurfacePosition.POSITION_DESKTOP_RIGHT;
			break;
		case "bottom":
			position = ISurfacePosition.POSITION_DESKTOP_BOTTOM;
			break;
		case "top":
			position = ISurfacePosition.POSITION_DESKTOP_TOP;
			break;
		}
		String url = "/security/service/portletResource.html";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		frame.parameter("position", position);

		m.site().out().flow(frame, circuit);
		if (circuit.content().readableBytes() > 0) {
			byte b[] = circuit.content().readFully();
			List<PortletSO> lets = new Gson().fromJson(new String(b),
					new TypeToken<List<PortletSO>>() {
					}.getType());
			return lets;
		}
		return new ArrayList<>(0);
	}

}
