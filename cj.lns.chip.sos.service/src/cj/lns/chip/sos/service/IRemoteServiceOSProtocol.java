package cj.lns.chip.sos.service;

import cj.studio.ecm.graph.CjStatusDef;
import cj.studio.ecm.graph.IConstans;

public interface IRemoteServiceOSProtocol extends IConstans{
	@CjStatusDef(message = "SOS/1.0")
	String PROTOCAL = "protocol";
}
