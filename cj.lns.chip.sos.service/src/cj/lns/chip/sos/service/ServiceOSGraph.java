package cj.lns.chip.sos.service;

import cj.lns.chip.sos.service.framework.IRemoteServiceFramework;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.Access;
import cj.studio.ecm.graph.AnnotationProtocolFactory;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.Graph;
import cj.studio.ecm.graph.GraphCreator;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.IProtocolFactory;
import cj.studio.ecm.graph.ISink;

@CjService(name = "serviceOSGraph", isExoteric = true)
public class ServiceOSGraph extends Graph {
	@Override
	protected String defineAcceptProptocol() {
		// TODO Auto-generated method stub
		return ".*";
	}

	@Override
	protected GraphCreator newCreator() {
		// TODO Auto-generated method stub
		return new ServiceOSGraphCreator();
	}
//	
//	System.out.println("print IAssemblyPlugins:"+root);
	// root.installPlugin("");
	// root.uninstallPlugin("");
	
	@Override
	protected void build(GraphCreator creator) {
		
		ISink remoteServiceFramework=creator.newSink("remoteServiceFramework");
		IPin innerPin=creator.newWirePin("innerPin",Access.middle);
		innerPin.plugLast("remoteServiceFramework",remoteServiceFramework);
		
		IPin input= creator.newWirePin("input", Access.input);
		input.plugLast("processExtenalSink", creator.newSink("processExtenalSink"));
		input.plugLast("remoteServiceFramework", remoteServiceFramework);
		//为模块提供中转服务，让模块也能请求平台的远程服务
		SendServiceCenter connect=(SendServiceCenter)site().getService("sendServiceCenter");
		connect.pin=innerPin;
	}

	class ServiceOSGraphCreator extends GraphCreator {
		
		@Override
		protected IProtocolFactory newProtocol() {
			return AnnotationProtocolFactory.factory(IRemoteServiceOSProtocol.class);
		}

		@Override
		protected ISink createSink(String sink) {
			if("remoteServiceFramework".equals(sink)){
				IRemoteServiceFramework framework= (IRemoteServiceFramework)site().getService("remoteServiceFramework");
				return framework;
			}
			if("processExtenalSink".equals(sink)){//处理外部来的请求
				return new ISink() {
					
					@Override
					public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
						if ("NET/1.1".equals(frame.protocol())) {
							return;
						}
						frame.head("original-root",frame.rootName());
						frame.url(frame.relativeUrl());
						plug.flow(frame, circuit);
					}
				};
			}
			return null;
		}

	}
}
