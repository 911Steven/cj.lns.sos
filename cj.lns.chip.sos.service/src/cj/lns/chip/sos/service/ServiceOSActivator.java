package cj.lns.chip.sos.service;

import cj.studio.ecm.IEntryPointActivator;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.plugins.AssemblyPluginActivator;
import cj.studio.ecm.plugins.IAssemblyPlugins;
import cj.studio.ecm.plugins.moduleable.IModuleContainer;
import cj.studio.ecm.resource.IResource;

public class ServiceOSActivator extends AssemblyPluginActivator
		implements IEntryPointActivator {
	@Override
	protected IModuleContainer getModuleContainer(IServiceSite site) {
		return (IModuleContainer) site
				.getService("pluginRemoteServiceContainer");
	}

	@Override
	protected IAssemblyPlugins loadPluginsBefore(IServiceSite site) {
		IResource my = (IResource) site.getService("myResource");
		if (my != null) {
			IResource the = (IResource) this.getClass().getClassLoader();
			if (the != null) {
				the.getPipeline().addReference(my);
			}
		}

		IAssemblyPlugins plugins = super.loadPluginsBefore(site);
		return plugins;
	}

	@Override
	protected void loadPluginsAfter(IAssemblyPlugins root, IServiceSite site) {
		// TODO Auto-generated method stub
		super.loadPluginsAfter(root, site);
	}
}
