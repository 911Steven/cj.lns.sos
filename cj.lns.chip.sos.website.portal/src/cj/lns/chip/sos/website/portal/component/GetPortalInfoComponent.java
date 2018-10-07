package cj.lns.chip.sos.website.portal.component;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.portal.PortalInfoScanner;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.IChip;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
@CjService(name="/getPortalInfoComponent.service")
public class GetPortalInfoComponent implements IComponent{
	@CjServiceRef
	PortalInfoScanner portalInfoScanner;
	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		String portalid=frame.parameter("portal.id");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IChip chip=m.subordinateChip(portalid);
		
		circuit.attribute("return-portal-chip",chip);
		circuit.attribute("return-portal-scanner",this.portalInfoScanner);
	}

}
