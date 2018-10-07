package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sws_authSubject database table.
 * 
 */
@Entity
@Table(name="sws_authSubject")
@NamedQuery(name="SwsAuthSubject.findAll", query="SELECT s FROM SwsAuthSubject s")
public class SwsAuthSubject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private BigInteger swsId;

	private String who;

	public SwsAuthSubject() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

	public String getWho() {
		return this.who;
	}

	public void setWho(String who) {
		this.who = who;
	}

}