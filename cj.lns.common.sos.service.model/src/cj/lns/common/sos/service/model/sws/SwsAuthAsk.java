package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the sws_authAsk database table.
 * 
 */
@Entity
@Table(name="sws_authAsk")
@NamedQuery(name="SwsAuthAsk.findAll", query="SELECT s FROM SwsAuthAsk s")
public class SwsAuthAsk implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private String answer;

	private String ask;

	private BigInteger permissionId;

	private BigInteger swsId;

	public SwsAuthAsk() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAsk() {
		return this.ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public BigInteger getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(BigInteger permissionId) {
		this.permissionId = permissionId;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

}