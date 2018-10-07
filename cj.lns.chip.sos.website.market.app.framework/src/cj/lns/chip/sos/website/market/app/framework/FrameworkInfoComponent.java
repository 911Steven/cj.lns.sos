package cj.lns.chip.sos.website.market.app.framework;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.framework.IFrameworkInfoReporter;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/frameworkInfo.html")
public class FrameworkInfoComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		Document doc=m.context().html("/frameworkInfo.html", m.site().contextPath(), "utf-8");
		circuit.content().writeBytes(doc.toString().getBytes());
	}
	private void render(Circuit circuit) {
		IServiceProvider site=ServiceosWebsiteModule.get().site();
		IFrameworkInfoReporter fr=(IFrameworkInfoReporter)site.getService("frameworkInfoReporter");
		cj.lns.chip.sos.website.framework.info.FrameworkInfo info=fr.get();
		String json=new Gson().toJson(info);
		circuit.content().writeBytes(json.getBytes());
	}
}
