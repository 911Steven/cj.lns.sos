package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AssignNewSwsParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="用户标识")
	String userCode;
	@CjRemoteParameter(must=true,usage="已分配的视窗号")
	BigInteger swsId;
	@CjRemoteParameter(must=true,usage="要继承的视窗id")
	BigInteger inheritId;
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public BigInteger getSwsId() {
		return swsId;
	}
	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}
	public BigInteger getInheritId() {
		return inheritId;
	}
	public void setInheritId(BigInteger inheritId) {
		this.inheritId = inheritId;
	}
	
	
}
