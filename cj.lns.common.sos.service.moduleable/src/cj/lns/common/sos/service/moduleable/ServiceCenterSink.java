package cj.lns.common.sos.service.moduleable;

import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
@CjService(name="ServiceCenterSink")
public class ServiceCenterSink implements ISink{
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// TODO Auto-generated method stub
		if(!frame.containsParameter("cjtoken")){
			throw new CircuitException("503", "未指定令牌."+frame.url());
		}
		plug.flow(frame,circuit);
	}

}
