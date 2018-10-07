package cj.lns.chip.sos.website.security.component.service;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.security.ISecurityCenter;
import cj.lns.chip.sos.website.security.ISecurityResource;
import cj.lns.chip.sos.website.security.ISecurityResourceImpl;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/service/portletResource.html")
public class PortletResourceComponent implements IComponent {
	@CjServiceRef(refByName = "securityCenter")
	ISecurityCenter center;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		ISecurityResource sr = center.resource(ISurfacePosition.POSITION_DESKTOP);
		if(sr==null){
			return;
		}
		String position = frame.parameter("position");// 如果position为空则报异常。
		ISecurityResourceImpl layout=sr.resourceImpl("desktop.layout");
		if(layout==null){
			return ;
		}
		List<ISecurityObject> list = layout.childs("column", position, 
				ISubject.subject(frame), IServicewsContext.context(frame));
		List<Object> lets=new ArrayList<>();
		for(ISecurityObject so:list){
			lets.add(so.unwrapper());
		}
		circuit.content().writeBytes(new Gson().toJson(lets).getBytes());
	}

}
