package cj.lns.chip.sos.service.sws.security.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AuthParameters implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="问题标识")
	BigInteger askId;
	@CjRemoteParameter(must=true,usage="答案")
	String answer;

	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken=cjtoken;
	}

	public BigInteger getAskId() {
		return askId;
	}

	public void setAskId(BigInteger askId) {
		this.askId = askId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
