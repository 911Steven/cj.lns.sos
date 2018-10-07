package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class DenyParameters extends PermissionIdParameters{
	@CjRemoteParameter(must=true,usage="联系人用户代码")
	String userCode;
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
}
