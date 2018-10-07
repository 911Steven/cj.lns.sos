package cj.lns.common.sos.service.model.sws;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the sws_authLogger database table.
 * 
 */
@Entity
@Table(name="sws_authLogger")
@NamedQuery(name="SwsAuthLogger.findAll", query="SELECT s FROM SwsAuthLogger s")
public class SwsAuthLogger implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private BigInteger id;

	private byte authMethod;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	private byte isOwner;

	private String message;

	private byte passed;

	private String sender;
	private BigInteger permId;
	private BigInteger swsId;

	public SwsAuthLogger() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getPermId() {
		return permId;
	}
	public void setPermId(BigInteger permId) {
		this.permId = permId;
	}
	public byte getAuthMethod() {
		return this.authMethod;
	}

	public void setAuthMethod(byte authMethod) {
		this.authMethod = authMethod;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public byte getIsOwner() {
		return this.isOwner;
	}

	public void setIsOwner(byte isOwner) {
		this.isOwner = isOwner;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte getPassed() {
		return this.passed;
	}

	public void setPassed(byte passed) {
		this.passed = passed;
	}

	public String getSender() {
		return this.sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public BigInteger getSwsId() {
		return this.swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
	}

}