package cj.lns.chip.sos.website;

import cj.lns.chip.sos.website.framework.IHub;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IEntryPointActivator;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.logging.ILogging;
import cj.studio.ecm.plugins.AssemblyPluginActivator;
import cj.studio.ecm.plugins.IAssemblyPlugins;
import cj.studio.ecm.plugins.moduleable.IModuleContainer;

public class ServiceOSActivator extends AssemblyPluginActivator
		implements IEntryPointActivator {
	ILogging log;

	@Override
	protected IAssemblyPlugins loadPluginsBefore(IServiceSite site) {
		log = CJSystem.current().environment().logging();
		IAssemblyPlugins plugins = super.loadPluginsBefore(site);
		return plugins;
	}

	@Override
	protected IModuleContainer getModuleContainer(IServiceSite site) {
		IModuleContainer e = (IModuleContainer) site
				.getService("serviceosModuleContainer");
		return e;
	}

	@Override
	protected void loadPluginsAfter(IAssemblyPlugins root, IServiceSite site) {
		IHub hub = (IHub) site.getService("hub");
		try {
			hub.refresh();
			log.info(getClass(), "完成serviceos模块回路组装");
		} catch (Exception e) {
			log.error(getClass(),
					String.format("组装serviceos模块回路失败，原因：%s", e.toString()));
			throw new EcmException(e);
		}
	}

	@Override
	public void inactivate(IServiceSite site) {
		super.inactivate(site);
	}
}
