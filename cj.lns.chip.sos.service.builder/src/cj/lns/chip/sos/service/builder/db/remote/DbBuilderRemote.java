package cj.lns.chip.sos.service.builder.db.remote;

import cj.lns.chip.sos.service.builder.db.remote.parameter.DbInitParameters;
import cj.lns.chip.sos.service.builder.db.service.IDbBuilderService;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "服务操作系统构建服务")
@CjService(name = "/sos/builder/", isExoteric = true)
public class DbBuilderRemote implements IDbBuilderRemote, IRemoteService {
	@CjServiceRef
	IDbBuilderService dbBuilderService;

	@CjRemoteMethod(usage = "初始化服务操作系统", returnContentType = "text/json", returnUsage = "无返回")
	@Override
	public RemoteResult init(DbInitParameters parameters)
			throws CircuitException {
		dbBuilderService.init(parameters.getSosid());
		RemoteResult res = new RemoteResult(200, "成功初始化数据库");
		res.content().writeBytes(
				"{'dbName':'sosdb',dbVersion:'1.00.000'}".getBytes());
		return res;
	}

}
