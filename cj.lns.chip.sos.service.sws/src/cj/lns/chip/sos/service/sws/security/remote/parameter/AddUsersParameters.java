package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AddUsersParameters extends DeleteRoleParameters {
	@CjRemoteParameter(must=true,usage="用户代码，多个用,号分隔")
	String userCodes;
	public String getUserCodes() {
		return userCodes;
	}
	public void setUserCodes(String userCodes) {
		this.userCodes = userCodes;
	}
}
