package cj.lns.chip.sos.website.portlet.component;

import cj.lns.chip.sos.website.portlet.service.ISwsPortletRenderService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/")
public class PortletRenderComponent implements IComponent {
	@CjServiceRef(refByName="swsPortletRenderService")
	ISwsPortletRenderService render;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		render.renderToServicews(frame,circuit);
	}
	
}
