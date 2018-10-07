package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class InstallAppParameters extends AppParameters {
	@CjRemoteParameter(must = true, usage = "应用插件标识，如果是多个以,号隔开")
	String pluginIds;
	public InstallAppParameters() {
		// TODO Auto-generated constructor stub
	}
	public String getPluginIds() {
		return pluginIds;
	}
	
	public void setPluginIds(String pluginIds) {
		this.pluginIds = pluginIds;
	}
}
