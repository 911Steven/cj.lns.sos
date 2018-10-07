package cj.lns.chip.sos.service.sws.security;

import java.math.BigInteger;

public class AuthAskInfo {

	private BigInteger id;

	private String answer;

	private String ask;

	private BigInteger permissionId;

	private BigInteger swsId;

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
