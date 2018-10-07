package cj.lns.chip.sos.website;

import cj.lns.chip.sos.website.framework.FrameworkFixedServiceProvider;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.plugins.IPluginFrameworkReadyEvent;
import cj.studio.ecm.plugins.moduleable.ModuleContainer;

@CjService(name = "serviceosModuleContainer")
public class ServiceosModuleContainer extends ModuleContainer
		implements IPluginFrameworkReadyEvent {
	@CjServiceRef(refByName = "frameworkFixedServiceProvider")
	FrameworkFixedServiceProvider provider;
	@Override
	public void dispose() {
		provider = null;
	}


	@Override
	protected IServiceProvider getFrameworkServiceProvider() {
		return provider;
	}


}
