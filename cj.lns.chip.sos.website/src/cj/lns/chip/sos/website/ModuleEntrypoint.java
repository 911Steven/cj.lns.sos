package cj.lns.chip.sos.website;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.IChip;
import cj.studio.ecm.adapter.AdapterFactory;
import cj.studio.ecm.adapter.IActuator;
import cj.studio.ecm.adapter.IAdaptable;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.plugins.moduleable.IModuleEntrypoint;
@CjService(name="moduleEntrypoint")
public class ModuleEntrypoint implements IModuleEntrypoint {
	@Override
	public void entrypoint(IModule m) {
		IChip security=m.subordinateChip("security");
		if(security!=null){
			Object scanner=security.site().getService("securityResourceScanner");
			if(scanner!=null){
				IAdaptable a=AdapterFactory.createAdaptable(scanner);
				IActuator sos=a.getAdapter(IActuator.class);
				sos.exactCommand("scans",new Class<?>[]{IServiceosWebsiteModule.class},m);
			}
		}
	}
}
