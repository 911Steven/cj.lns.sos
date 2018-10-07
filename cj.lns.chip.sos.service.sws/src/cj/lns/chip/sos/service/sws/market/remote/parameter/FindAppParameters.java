package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class FindAppParameters extends AppParameters{
	@CjRemoteParameter(must = true, usage = "应用代码")
	String appCode;
	@CjRemoteParameter(must = true, usage = "应用提供商")
	String provider;
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
}
