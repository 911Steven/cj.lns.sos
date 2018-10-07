package cj.lns.chip.sos.service.builder.db.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class DbInitParameters  implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "服务操作系统编号")
	String sosid;
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String token) {
		// TODO Auto-generated method stub
		this.cjtoken=token;
	}
	public String getSosid() {
		return sosid;
	}
	public void setSosid(String sosid) {
		this.sosid = sosid;
	}
}
