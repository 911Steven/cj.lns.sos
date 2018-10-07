package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class UnmountAppParameters extends AppParameters{
	@CjRemoteParameter(must = true, usage = "即应用所在芯片标识")
	String provider;
	@CjRemoteParameter(must = true, usage = "应用代码")
	String appCode;
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
}
