package cj.lns.chip.sos.website.portlet.service;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;

public interface ISwsPortletRenderService {

	void renderToServicews(Frame frame, Circuit circuit) throws CircuitException;

}
