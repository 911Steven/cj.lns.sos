package cj.lns.chip.sos.website.security;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "securityResourceScanner")
public class SecurityResourceScanner implements ISecurityResourceScanner {
	@CjServiceRef(refByName="securityCenter")
	ISecurityCenter center;
	@Override
	public void scans(IServiceosWebsiteModule m) {
		center.init();
		center.scans(m);
	}
	
}
