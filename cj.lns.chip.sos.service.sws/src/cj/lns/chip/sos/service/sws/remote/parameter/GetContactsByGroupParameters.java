package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GetContactsByGroupParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "视窗标识")
	BigInteger swsid;
	@CjRemoteParameter(must = true, usage = "组物理标识")
	BigInteger groupId;
	public BigInteger getSwsid() {
		return swsid;
	}
	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String token) {
		// TODO Auto-generated method stub
		this.cjtoken = token;
	}
	public BigInteger getGroupId() {
		return groupId;
	}
	public void setGroupId(BigInteger groupId) {
		this.groupId = groupId;
	}
}
