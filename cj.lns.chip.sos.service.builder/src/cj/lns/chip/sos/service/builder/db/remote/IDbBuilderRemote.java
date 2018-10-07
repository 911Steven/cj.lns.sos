package cj.lns.chip.sos.service.builder.db.remote;

import cj.lns.chip.sos.service.builder.db.remote.parameter.DbInitParameters;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.studio.ecm.graph.CircuitException;

public interface IDbBuilderRemote {
	RemoteResult init(DbInitParameters parameters)throws CircuitException;
}
