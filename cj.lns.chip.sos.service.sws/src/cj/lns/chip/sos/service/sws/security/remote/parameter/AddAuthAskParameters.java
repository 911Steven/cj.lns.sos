package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AddAuthAskParameters extends PermissionIdParameters {
	@CjRemoteParameter(must=true,usage="问题标题")
	String ask;
	@CjRemoteParameter(must=true,usage="答案")
	String answer;
	public String getAsk() {
		return ask;
	}
	public void setAsk(String question) {
		this.ask = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
