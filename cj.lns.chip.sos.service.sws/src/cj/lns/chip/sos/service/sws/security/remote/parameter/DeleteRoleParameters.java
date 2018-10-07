package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class DeleteRoleParameters extends RoleParameters {
	@CjRemoteParameter(must=true,usage="角色代码")
	String roleCode;
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
}
