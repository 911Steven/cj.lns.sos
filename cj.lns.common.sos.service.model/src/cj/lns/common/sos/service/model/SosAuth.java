package cj.lns.common.sos.service.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sos_auth database table.
 * 
 */
@Entity
@Table(name="sos_auth")
@NamedQuery(name="SosAuth.findAll", query="SELECT s FROM SosAuth s")
public class SosAuth implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String account;

	private String authModeCode;

	private String data;

	private String mask;

	public SosAuth() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAuthModeCode() {
		return this.authModeCode;
	}

	public void setAuthModeCode(String authModeCode) {
		this.authModeCode = authModeCode;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMask() {
		return this.mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

}