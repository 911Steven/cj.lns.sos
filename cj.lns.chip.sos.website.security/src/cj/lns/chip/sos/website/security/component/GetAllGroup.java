package cj.lns.chip.sos.website.security.component;

import java.util.List;

import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.security.IContactGroupService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name="/getAllGroup.service")
public class GetAllGroup implements IComponent {
	@CjServiceRef(refByName="groupService")
	IContactGroupService group;
	@Override
	public void flow(Frame frame, Circuit circuit,IPlug plug) throws CircuitException {
		IServicewsContext sws=IServicewsContext.context(frame);
		List<ContactGroupInfo> groups= group.getContactGroups(sws.swsid());
		String json=new Gson().toJson(groups);
		circuit.contentType(frame.head("Accept"));
		circuit.content().writeBytes(json.getBytes());
	}

}
