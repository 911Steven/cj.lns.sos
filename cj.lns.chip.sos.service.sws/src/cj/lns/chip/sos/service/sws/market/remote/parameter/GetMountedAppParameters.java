package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GetMountedAppParameters extends   AppParameters{
	@CjRemoteParameter(must = false, usage = "插入界面的位置")
	String position;
	@CjRemoteParameter(must = false, usage = "提供器的模块名")
	String provider;
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
}
