package cj.lns.chip.sos.service;

import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
/**
 * 为模块提供中转服务，让模块也能请求平台的远程服务
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
@CjService(name="sendServiceCenter")
public class SendServiceCenter implements ISink{
	IPin pin;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		pin.flow(frame, circuit);
	}
}
