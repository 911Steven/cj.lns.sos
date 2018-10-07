package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GetSwsIdParameters implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="令牌")
	private String cjtoken;
	@CjRemoteParameter(must=true,usage="源视窗")
	private BigInteger sourceSwsid;
	@CjRemoteParameter(must=true,usage="目标用户")
	private String toUser;
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}
	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken=cjtoken;
	}
	public BigInteger getSourceSwsid() {
		return sourceSwsid;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public void setSourceSwsid(BigInteger sourceSwsid) {
		this.sourceSwsid = sourceSwsid;
	}
	public String getToUser() {
		return toUser;
	}
}
