package cj.lns.chip.sos.service.sws.security.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class DelQuestionParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="资源标识")
	String objectName;
	@CjRemoteParameter(must=true,usage="资源值")
	String objectInst;
	@CjRemoteParameter(must=true,usage="问题文本")
	String a;
	@CjRemoteParameter(must=true,usage="答案文本")
	String q;
	@CjRemoteParameter(must=true,usage="许可代码")
	String permissionCode;
	@CjRemoteParameter(must=true,usage="视窗")
	BigInteger swsId;
	@CjRemoteParameter(must=false,usage="问题物理标识")
	BigInteger aqid;
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

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public BigInteger getAqid() {
		return aqid;
	}
	public void setAqid(BigInteger aqid) {
		this.aqid = aqid;
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

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public BigInteger getSwsId() {
		return swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}
}
