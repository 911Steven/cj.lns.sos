package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sws_authPassword database table.
 * 
 */
@Entity
@Table(name="sws_authPassword")
@NamedQuery(name="SwsAuthPassword.findAll", query="SELECT s FROM SwsAuthPassword s")
public class SwsAuthPassword implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String password;

	private BigInteger swsId;

	private BigInteger who;

	public SwsAuthPassword() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

	public BigInteger getWho() {
		return this.who;
	}

	public void setWho(BigInteger who) {
		this.who = who;
	}

}