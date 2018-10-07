package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class SetDefaultAppParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "视窗标识")
	BigInteger swsid;
	@CjRemoteParameter(must = true, usage = "应用物理标识")
	String appId;
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
	public BigInteger getSwsid() {
		return swsid;
	}
	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
}
