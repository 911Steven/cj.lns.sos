package cj.lns.common.sos.website.customable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.Access;
import cj.studio.ecm.graph.AnnotationProtocolFactory;
import cj.studio.ecm.graph.Graph;
import cj.studio.ecm.graph.GraphCreator;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IProtocolFactory;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.plugins.moduleable.IModule;

class ModuleCustomableGraph extends Graph {
	IModule m;
	Map<String, IComponent> components;

	public ModuleCustomableGraph(IModule m) {
		this.m = m;
	}

	@Override
	protected String defineAcceptProptocol() {
		return ".*";
	}

	@Override
	protected void build(GraphCreator creator) {
		ServiceCollection<IComponent> col = m.chip().site()
				.getServices(IComponent.class);
		components = new HashMap<>();
		for (IComponent p : col) {
			CjService cs = p.getClass().getAnnotation(CjService.class);
//			if (!cs.name().contains("://")) {
//				throw new EcmException(String
//						.format("组件服务名必须包含协议符，格式：协议://路径。在：%s", cs.name()));
//			}
			String name = cs.name();
			if (name.endsWith("/")&&!"/".equals(name)) {
				name = name.substring(0, name.length()-1);
			}
			components.put(name, p);
		}

		IPin serviceosInput = creator.newWirePin("serviceosInput",
				Access.input);
		IPin upriverInput = creator.newWirePin("upriverInput", Access.input);

		m.site().in()
				.plugLast("acceptServiceosIn",
						creator.newSink("acceptServiceosIn"))
				.plugBranch("out", serviceosInput);
		m.in().plugLast("acceptUpriverIn", creator.newSink("acceptUpriverIn"))
				.plugBranch("out", upriverInput);
		// 在此中间插入过滤器
		ServiceCollection<IComponentFilter> filters = m.chip().site()
				.getServices(IComponentFilter.class);
		if (!filters.isEmpty()) {
			List<IComponentFilter> sorted = filters.asList();
			sorted.sort(new Comparator<IComponentFilter>() {
				@Override
				public int compare(IComponentFilter o1, IComponentFilter o2) {
					return o1.sort() - o2.sort();
				}
			});
			for (IComponentFilter filter : sorted) {
				CjService cs = filter.getClass().getAnnotation(CjService.class);
				switch (filter.matchCircuit()) {
				case -1:
					upriverInput.plugLast(cs.name(), filter);
					break;
				case 1:
					serviceosInput.plugLast(cs.name(), filter);
					break;
				default:
					serviceosInput.plugLast(cs.name(), filter);
					upriverInput.plugLast(cs.name(), filter);
					break;
				}
			}
		}
		serviceosInput.plugLast("dispatchSink",
				creator.newSink("dispatchSink"));
		upriverInput.plugLast("dispatchSink", creator.newSink("dispatchSink"));
	}

	@Override
	protected GraphCreator newCreator() {
		return new MyCreator();
	}

	class MyCreator extends GraphCreator {
		@Override
		protected IProtocolFactory newProtocol() {
			return AnnotationProtocolFactory
					.factory(IComponentDispatherConstants.class);
		}

		@Override
		protected ISink createSink(String sink) {
			if ("acceptServiceosIn".equals(sink)) {
				return new AcceptServiceosIn();
			}
			if ("acceptUpriverIn".equals(sink)) {
				return new AcceptUpriverIn();
			}
			if ("dispatchSink".equals(sink)) {
				return new ComponentDispatcher(components);
			}
			return null;
		}

	}
}
