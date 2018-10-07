package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the sws_permission database table.
 * 
 */
@Entity
@Table(name = "sws_permission")
@NamedQuery(name = "SwsPermission.findAll", query = "SELECT s FROM SwsPermission s")
public class SwsPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;

	private String objectInst;

	private String objectName;
	private String permissionCode;
	private String permissionName;
	private byte authMethod;

	private BigInteger swsId;

	public SwsPermission() {
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getObjectInst() {
		return this.objectInst;
	}

	public void setObjectInst(String objectInst) {
		this.objectInst = objectInst;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getPermissionCode() {
		return this.permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public byte getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(byte authMethod) {
		this.authMethod = authMethod;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

}