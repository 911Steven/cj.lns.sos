package cj.lns.chip.sos.service.sws.security;

import java.math.BigInteger;
import java.util.Date;


public class AuthLoggerInfo {

	private BigInteger id;

	private byte authMethod;

	private Date createDate;

	private byte isOwner;

	private String message;

	private byte passed;

	private String sender;
	private BigInteger permId;
	private BigInteger swsId;

	public AuthLoggerInfo() {
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