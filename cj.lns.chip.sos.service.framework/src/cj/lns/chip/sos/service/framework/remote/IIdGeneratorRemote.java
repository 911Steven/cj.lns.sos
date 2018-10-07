package cj.lns.chip.sos.service.framework.remote;

import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.remote.parameter.GenNumParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GenServicewsNumParameters;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.studio.ecm.graph.CircuitException;

public interface IIdGeneratorRemote {

	RemoteResult genNum(GenNumParameters parameters) throws CircuitException;

	RemoteResult genServicewsNum(GenServicewsNumParameters parameters)
			throws CircuitException;

}