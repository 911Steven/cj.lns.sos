package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sws_role database table.
 * 
 */
@Entity
@Table(name="sws_role")
@NamedQuery(name="SwsRole.findAll", query="SELECT s FROM SwsRole s")
public class SwsRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String description;

	private String roleCode;

	private String roleName;

	private BigInteger swsId;
	private byte propagate;
	public byte getPropagate() {
		return propagate;
	}
	public void setPropagate(byte propagate) {
		this.propagate = propagate;
	}
	public SwsRole() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

}