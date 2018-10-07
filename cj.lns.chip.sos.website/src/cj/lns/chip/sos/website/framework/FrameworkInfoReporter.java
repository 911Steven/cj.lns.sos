package cj.lns.chip.sos.website.framework;

import cj.lns.chip.sos.website.framework.info.FrameworkInfo;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.annotation.CjServiceSite;
import cj.studio.ecm.plugins.IAssemblyPlugins;
@CjService(name="frameworkInfoReporter")
public class FrameworkInfoReporter implements IFrameworkInfoReporter{
	@CjServiceRef(refByName="frameworkScanner")
	FrameworkScanner scanner;
	@CjServiceSite
	IServiceSite _temp;
	@Override
	public FrameworkInfo get() {
		IAssemblyPlugins root=(IAssemblyPlugins)_temp.getService(IAssemblyPlugins.KEY_ASSEMBLY_PLUGIN);
		return scanner.scan(root);
	}
}