package cj.lns.chip.sos.service.sws.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class RemoveContactParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="视窗标识")
	Long swsid;
	@CjRemoteParameter(must=true,usage="sos用户标识")
	String userCode;
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
}
