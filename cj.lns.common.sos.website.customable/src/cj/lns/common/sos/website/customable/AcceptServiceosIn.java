package cj.lns.common.sos.website.customable;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;

class AcceptServiceosIn implements ISink {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		plug.branch("out").flow(frame, circuit);
	}

}
