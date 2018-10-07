package cj.lns.chip.sos.website.security.component;

import java.util.List;

import cj.lns.chip.sos.service.sws.user.ContactInfo;
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
import cj.ultimate.util.StringUtil;

@CjService(name="/getContactsByGroup.service")
public class GetContactsByGroup implements IComponent {
	@CjServiceRef(refByName="groupService")
	IContactGroupService group;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String gid=frame.parameter("gid");
		if(StringUtil.isEmpty(gid)){
			throw new CircuitException("503", "参数组标识未指定");
		}
		//Accept: application/json, text/javascript, */*; q=0.01
		IServicewsContext sws=IServicewsContext.context(frame);
		List<ContactInfo> users= group.getContactsByGroup(gid,sws.swsid());
		String json=new Gson().toJson(users);
		circuit.contentType(frame.head("Accept"));
		circuit.content().writeBytes(json.getBytes());
	}

}
