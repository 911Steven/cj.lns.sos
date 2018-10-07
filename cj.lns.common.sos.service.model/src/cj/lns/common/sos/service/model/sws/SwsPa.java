package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sws_pa database table.
 * 
 */
@Entity
@Table(name="sws_pa")
@NamedQuery(name="SwsPa.findAll", query="SELECT s FROM SwsPa s")
public class SwsPa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private BigInteger permissionId;

	private String subjectInst;

	private String subjectName;

	public SwsPa() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(BigInteger permissionId) {
		this.permissionId = permissionId;
	}

	public String getSubjectInst() {
		return this.subjectInst;
	}

	public void setSubjectInst(String subjectInst) {
		this.subjectInst = subjectInst;
	}

	public String getSubjectName() {
		return this.subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

}