package cj.lns.chip.sos.service.sws.security;

import java.math.BigInteger;

/**
 * 许可的定义
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
public class PermissionInfo {
	private BigInteger permissionId;
	private String objectInst;
	private String objectName;
	private String permissionCode;
	private ServicewsAuthMethod authMethod = ServicewsAuthMethod.none;
	private BigInteger swsId;
	private boolean isDirObject;
	public BigInteger getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(BigInteger permissionId) {
		this.permissionId = permissionId;
	}
	public String getObjectInst() {
		return objectInst;
	}

	public void setObjectInst(String objectInst) {
		this.objectInst = objectInst;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public ServicewsAuthMethod getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(ServicewsAuthMethod authMethod) {
		this.authMethod = authMethod;
	}

	public BigInteger getSwsId() {
		return swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}


	public boolean isDirObject() {
		return isDirObject;
	}

	public void setDirObject(boolean isDirObject) {
		this.isDirObject = isDirObject;
	}

}
