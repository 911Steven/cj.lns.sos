package cj.lns.chip.sos.service.sws.market.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class UnmountMenuParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "即栏目所在芯片标识")
	String provider;
	@CjRemoteParameter(must = true, usage = "栏目代码")
	String code;
	@CjRemoteParameter(must = true, usage = "视窗标识")
	BigInteger swsId;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	
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
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
