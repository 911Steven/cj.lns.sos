package cj.lns.common.sos.website.moduleable;

import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.Access;
import cj.studio.ecm.graph.AnnotationProtocolFactory;
import cj.studio.ecm.graph.GraphCreator;
import cj.studio.ecm.graph.IProtocolFactory;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.plugins.moduleable.ModuleConstans;
import cj.studio.ecm.plugins.moduleable.ModuleGraph;

@CjService(name = "serviceosWebsiteModuleGraph")
public class ServiceosWebsiteModuleGraph extends ModuleGraph implements IServiceAfter{
	@Override
	public void onAfter(IServiceSite site) {
		if(!super.isInit())
		super.initGraph();
		ModuleServiceSite m=(ModuleServiceSite)site.getService("moduleServiceSite");
		m.out=out("frameworkOutput");
		m.in=in("frameworkInput");
		
	}
	@Override
	protected void build(GraphCreator creator) {
		super.build(creator);
		creator.newWirePin("frameworkOutput", Access.output);
		creator.newWirePin("frameworkInput", Access.input);
	}

	@Override
	protected GraphCreator newCreator() {

		return new WebsiteModuleGraphCreator();
	}

	class WebsiteModuleGraphCreator extends GraphCreator {
		@Override
		protected IProtocolFactory newProtocol() {
			return AnnotationProtocolFactory.factory(ModuleConstans.class);
		}

		@Override
		protected ISink createSink(String sink) {
			return null;
		}

	}
}
