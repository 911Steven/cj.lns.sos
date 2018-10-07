package cj.lns.common.sos.service.moduleable;

import cj.lns.chip.sos.service.framework.IServiceosModuleServiceProvider;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.studio.ecm.IChip;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjMethod;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
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
@CjExotericalType(typeName = "serviceosServiceModule")
@CjService(name = "serviceosServiceModule", isExoteric = true,constructor="get")
public class ServiceosServiceModule extends Module
		implements IModule, IServiceosServiceModule {
	IChip chip;

	@CjServiceRef
	ModuleServiceSite moduleServiceSite;

	@CjServiceRef(refByName = "serviceosServiceModuleGraph")
	IModuleGraph graph;
	static IServiceosServiceModule current;

	static {
		current = new ServiceosServiceModule();
	}

	@CjMethod(returnDefinitionId = ".")
	public static IServiceosServiceModule get() {
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


	@Override
	protected IModuleGraph graph() {
		return graph;
	}

}