package cj.lns.chip.sos.service.sws.security.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class ObjectAllPermissionsParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "视窗")
	BigInteger swsid;
	@CjRemoteParameter(must = true, usage = "用户代码")
	String userCode;
	@CjRemoteParameter(must = true, usage = "资源对象名")
	String objectName;
	@CjRemoteParameter(must = true, usage = "具体的一个资源对象")
	String objectInst;

	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken = cjtoken;
	}

	public BigInteger getSwsid() {
		return swsid;
	}

	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectInst() {
		return objectInst;
	}

	public void setObjectInst(String objectInst) {
		this.objectInst = objectInst;
	}
	
}
