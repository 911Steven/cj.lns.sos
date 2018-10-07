package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AddRoleSwsParameters extends RoleParameters {
	@CjRemoteParameter(must=true,usage="角色代码")
	String roleCode;
	@CjRemoteParameter(must=true,usage="角色名称")
	String roleName;
	@CjRemoteParameter(must=false,usage="描述")
	String desc;
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
