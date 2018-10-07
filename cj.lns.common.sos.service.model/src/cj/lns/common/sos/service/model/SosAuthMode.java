package cj.lns.common.sos.service.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the sos_auth_mode database table.
 * 
 */
@Entity
@Table(name="sos_auth_mode")
@NamedQuery(name="SosAuthMode.findAll", query="SELECT s FROM SosAuthMode s")
public class SosAuthMode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	private String authImpl;

	private String modeCode;

	private String modeName;

	public SosAuthMode() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getAuthImpl() {
		return this.authImpl;
	}

	public void setAuthImpl(String authImpl) {
		this.authImpl = authImpl;
	}

	public String getModeCode() {
		return this.modeCode;
	}

	public void setModeCode(String modeCode) {
		this.modeCode = modeCode;
	}

	public String getModeName() {
		return this.modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

}