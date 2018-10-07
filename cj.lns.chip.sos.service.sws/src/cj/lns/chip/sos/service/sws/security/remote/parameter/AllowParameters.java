package cj.lns.chip.sos.service.sws.security.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AllowParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "许可标识")
	BigInteger permissionId;
	@CjRemoteParameter(must = true, usage = "主体名")
	String subjectName;
	@CjRemoteParameter(must = true, usage = "主题实例")
	String subjectInst;

	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken = cjtoken;
	}

	public BigInteger getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(BigInteger permissionId) {
		this.permissionId = permissionId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectInst() {
		return subjectInst;
	}

	public void setSubjectInst(String subjectInst) {
		this.subjectInst = subjectInst;
	}
	
}
