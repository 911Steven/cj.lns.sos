package cj.lns.common.sos.service.moduleable;

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

@CjService(name = "serviceosServiceModuleGraph")
public class ServiceosServiceModuleGraph extends ModuleGraph implements IServiceAfter{
	@Override
	public void onAfter(IServiceSite site) {
		if(!isInit())
		initGraph();
		ModuleServiceSite m=(ModuleServiceSite)site.getService("moduleServiceSite");
		m.out=out("frameworkOutput");
		m.in=in("frameworkInput");
	}
	@Override
	protected void build(GraphCreator creator) {
		super.build(creator);
		creator.newWirePin("frameworkOutput", Access.output).plugLast(
				"serviceCenterSink", creator.newSink("serviceCenterSink"));
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
			if ("serviceCenterSink".equals(sink)) {
				return new ServiceCenterSink();
			}
			return null;
		}

	}
}
