package cj.lns.chip.sos.service.sws.security.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AllowRoleParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "视窗号")
	BigInteger swsId;
	@CjRemoteParameter(must = true, usage = "资源名")
	String objectName;
	@CjRemoteParameter(must = true, usage = "资源实例")
	String objectInst;
	@CjRemoteParameter(must = true, usage = "角色代码")
	String roleCode;
	@CjRemoteParameter(must = true, usage = "许可代码")
	String permissionCode;
	@CjRemoteParameter(must = true, usage = "许可名")
	String permissionName;
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
	public BigInteger getSwsId() {
		return swsId;
	}
	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
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
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getPermissionCode() {
		return permissionCode;
	}
	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	
}
