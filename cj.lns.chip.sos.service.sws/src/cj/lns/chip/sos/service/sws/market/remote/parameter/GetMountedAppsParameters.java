package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GetMountedAppsParameters extends   AppParameters{
	@CjRemoteParameter(must = false, usage = "插入界面的位置")
	String position;
	@CjRemoteParameter(must = false, usage = "提供器的模块名，格式：v1,v2,v3")
	String providers;
	@CjRemoteParameter(must = false, usage = "应用所属平台")
	String platform;
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getProviders() {
		return providers;
	}
	public void setProviders(String providers) {
		this.providers = providers;
	}
}
