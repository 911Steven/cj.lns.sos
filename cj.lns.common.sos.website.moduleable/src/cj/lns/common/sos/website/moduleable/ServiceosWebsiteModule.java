package cj.lns.common.sos.website.moduleable;

import cj.lns.chip.sos.website.framework.IServiceosModuleServiceProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.IChip;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjMethod;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.plugins.moduleable.IModuleGraph;
import cj.studio.ecm.plugins.moduleable.Module;

/**
 * 持久化模块
 * 
 * <pre>
 * 各插件内部使用。
 * </pre>
 * 
 * @author carocean
 *
 */
@CjExotericalType(typeName = "serviceosWebsiteModule")
@CjService(name = "serviceosWebsiteModule", isExoteric = true,constructor="get")
public class ServiceosWebsiteModule extends Module
		implements IModule, IServiceosWebsiteModule {
	IChip chip;

	@CjServiceRef
	ModuleServiceSite moduleServiceSite;

	@CjServiceRef(refByName = "serviceosWebsiteModuleGraph")
	IModuleGraph graph;
	static IServiceosWebsiteModule current;

	static {
		current = new ServiceosWebsiteModule();
	}

	@CjMethod(returnDefinitionId = ".")
	public static IServiceosWebsiteModule get() {
		return current;
	}

	@Override
	public IChip chip() {
		if (chip == null)
			chip = (IChip) moduleServiceSite.getService(IChip.class.getName());
		return chip;
	}

	@Override
	public IServiceosModuleServiceProvider site() {
		return moduleServiceSite;
	}

	public IPin persistenceOut() {
		return (IPin) moduleServiceSite.getService("persistenceOut");
	}

	@Override
	protected IModuleGraph graph() {
		return graph;
	}

}