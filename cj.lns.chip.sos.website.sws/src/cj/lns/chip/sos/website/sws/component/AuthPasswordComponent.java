package cj.lns.chip.sos.website.sws.component;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.plugins.moduleable.IModuleContext;
@CjService(name="/authPassword.html")
public class AuthPasswordComponent implements IComponent{

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// TODO Auto-generated method stub
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		IModuleContext ctx= m.context();
		Document doc= ctx.html("/auth-password.html",m.site().contextPath(), "utf-8");
		circuit.content().writeBytes(doc.toString().getBytes());
	}

}
