package cj.lns.chip.sos.service.sws.market.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class FindPortletParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "视窗标识")
	BigInteger swsId;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "栏目物理标识")
	BigInteger phyId;
	public BigInteger getSwsId() {
		return swsId;
	}
	public void setSwsId(BigInteger swsId) {
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
	public BigInteger getPhyId() {
		return phyId;
	}
	public void setPhyId(BigInteger phyId) {
		this.phyId = phyId;
	}
}
