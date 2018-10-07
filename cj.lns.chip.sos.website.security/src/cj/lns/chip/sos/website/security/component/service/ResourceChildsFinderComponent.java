package cj.lns.chip.sos.website.security.component.service;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.ISecurityObject;
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

@CjService(name = "/resourceChildsFinder.service")
public class ResourceChildsFinderComponent implements IComponent {
	@CjServiceRef(refByName = "securityCenter")
	ISecurityCenter center;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String resourceId = frame.parameter("resourceId");
		String valueId = frame.parameter("valueId");
		String rootResourceId = frame.parameter("rootResourceId");
		String rootValueId = frame.parameter("rootValueId");
		if (!center.containsResource(rootResourceId)) {
			throw new CircuitException("404",
					String.format("资源%s不存在", rootResourceId));
		}
		ISecurityResource sr = center.resource(rootResourceId);
		ISecurityResourceImpl impl = sr.resourceImpl(rootValueId);
		if (impl == null) {
			throw new CircuitException("404",
					String.format("资源%s中不存在实现%s", rootResourceId, rootValueId));
		}
		List<ISecurityObject> childs = impl.childs(resourceId, valueId,
				ISubject.subject(frame), IServicewsContext.context(frame));
		List<Object> list = new ArrayList<>();
		if (childs != null) {
			for (ISecurityObject o : childs) {
				list.add(o.unwrapper());
			}
		}
		String json = new Gson().toJson(list);
		circuit.content().writeBytes(json.getBytes());
	}

}
