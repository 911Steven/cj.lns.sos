package cj.lns.chip.sos.website.security.component;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.security.IContactGroupService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name="/contactPad.html")
public class ContactPad implements IComponent{
	@CjServiceRef(refByName="groupService")
	IContactGroupService group;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		IServicewsContext ctx=IServicewsContext.context(frame);
		Document doc = m.context().html("/contactPad.html",
				m.site().contextPath(), "utf-8");
		renderGroups(doc,ctx.swsid());
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void renderGroups(Document doc, String swsid) throws CircuitException {
		Element gregion=doc.select(".group-box[groups]").first();
		Element li=gregion.select("li").first().clone();
		gregion.empty();
		List<ContactGroupInfo> groups=group.getContactGroups(swsid);
		for(ContactGroupInfo g:groups){
			li=li.clone();
			li.attr("cjevent",g.getId().toString());
			li.select("span").html(g.getGroupName());
			gregion.appendChild(li);
		}
	}

}
