package cj.lns.chip.sos.website.market.app.controlpanel.component;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IWebsiteConstants;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.market.app.controlpanel.provider.ControlPanelSoProvider;
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

@CjService(name = "/")
public class IndexComponent implements IComponent {
	@CjServiceRef(refByName = "controlPanelSoProvider")
	ControlPanelSoProvider provider;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();

		if (frame.containsParameter("openapp")) {
			openApp(frame, circuit, m);
			return;
		}
		openHome(frame, circuit, m);
	}
	
	private void openHome(Frame frame, Circuit circuit,
			IServiceosWebsiteModule m) throws CircuitException {
		Document doc = m.context().html("/index.html", m.site().contextPath(),
				"utf-8");
		List<?> apps = provider.getChilds("application", String.valueOf(IWebsiteConstants.FIXED_APP_CONTROLPANEL),
				ISubject.subject(frame), IServicewsContext.context(frame));
		Element ul = doc.select(".ctr-panel>.content>ul.panel-body").first();
		Element li = ul.select("li.panel-item").first().clone();
		ul.empty();
		for (Object o : apps) {
			li = li.clone();
			AppSO a = (AppSO) o;
			li.attr("openapp", a.getPhyId().toString());
			li.select("img").attr("src", a.getIcon());
			li.select(".item>.title").html(a.getName());
			if (a.getDesc() == null)
				li.select(".item>.desc").html("");
			else
				li.select(".item>.desc").html(a.getDesc());
			ul.appendChild(li);
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void openApp(Frame frame, Circuit circuit,
			IServiceosWebsiteModule m) throws CircuitException {
		String appId = frame.parameter("openapp");
		ISubject subject = ISubject.subject(frame);
		AppSO app = (AppSO) provider.find("application", appId, subject,
				IServicewsContext.context(frame));
		if (app == null) {
			throw new CircuitException("404", "应用不存在：" + appId);
		}
		String url=app.getCommand();
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		 frame.url(url);
//		frame.url("./servicews/market/swsSettingPanel.html");
		m.site().out().flow(frame, circuit);

	}

}
