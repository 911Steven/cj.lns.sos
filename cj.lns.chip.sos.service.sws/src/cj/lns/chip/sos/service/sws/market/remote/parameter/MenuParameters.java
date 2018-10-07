package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class MenuParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "视窗标识")
	String swsId;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	
	public String getSwsId() {
		return swsId;
	}
	public void setSwsId(String swsId) {
		this.swsId = swsId;
	}
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
}
