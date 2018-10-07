package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class InstallMenuParameters extends MenuParameters {
	@CjRemoteParameter(must = true, usage = "菜单插件标识")
	String menuPluginGuid;

	public String getMenuPluginGuid() {
		return menuPluginGuid;
	}

	public InstallMenuParameters() {
		// TODO Auto-generated constructor stub
	}

	public void setMenuPluginGuid(String menuPluginGuid) {
		this.menuPluginGuid = menuPluginGuid;
	}

}
