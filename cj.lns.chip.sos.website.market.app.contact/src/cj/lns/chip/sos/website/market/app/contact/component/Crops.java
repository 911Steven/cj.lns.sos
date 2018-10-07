package cj.lns.chip.sos.website.market.app.contact.component;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
@CjService(name = "/crops.html")
public class Crops implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/contact/crops.html",
				m.site().contextPath(), "utf-8");
		circuit.content().writeBytes(doc.toString().getBytes());
		
	}

}
