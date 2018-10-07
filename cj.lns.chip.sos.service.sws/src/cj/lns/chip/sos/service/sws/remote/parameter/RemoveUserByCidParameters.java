package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class RemoveUserByCidParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="视窗标识")
	Long swsid;
	@CjRemoteParameter(must=true,usage="联系人物理标识")
	BigInteger cid;
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@Override
	public String cjtoken() {
		return cjtoken;
	}
	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken=cjtoken;
	}
	public Long getSwsid() {
		return swsid;
	}
	public void setSwsid(Long swsid) {
		this.swsid = swsid;
	}
	public BigInteger getCid() {
		return cid;
	}
	public void setCid(BigInteger cid) {
		this.cid = cid;
	}
	
}
