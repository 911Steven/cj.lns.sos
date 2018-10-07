package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class FindAppCodeParameters extends AppParameters {
	@CjRemoteParameter(must = true, usage = "应用代码")
	String code;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
