package cj.lns.chip.sos.website.desktop.component;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.util.StringUtil;

@CjService(name = "/")
public class DesktopRenderComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// byte[] b = frame.content().readFully();
		// Document doc = Jsoup.parse(new String(b));
		// Elements es = doc.select(".desktop");
		// if (!es.isEmpty()) {
		// Element ewin=es.first();
		frame.content().clear();
//		renderDefaultApp(frame, circuit);
		byte[] b = circuit.content().readFully();
		Document win = Jsoup.parse(new String(b));
		// ewin.append(win.body().html());
		// if(win.head()!=null){
		// doc.head().html(win.head().html());
		// }
		// }
		if(StringUtil.isEmpty(win.body().html())){
			win.body().html("&nbsp;");
		}
		circuit.content().writeBytes(win.toString().getBytes());
	}

//	private void renderDefaultApp(Frame frame, Circuit circuit)
//			throws CircuitException {
//		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
//		String old = frame.url();
//		String url="/servicews/defaultAppRender.html";
//		if (frame.containsQueryString()) {
//			url = String.format("%s?%s", url, frame.queryString());
//		}
//		frame.url(url);
//		try {
//			m.site().out().flow(frame, circuit);
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			frame.url(old);
//		}
//	}

}
