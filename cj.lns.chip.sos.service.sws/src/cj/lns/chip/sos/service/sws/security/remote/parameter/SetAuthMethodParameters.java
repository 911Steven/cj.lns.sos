package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;
import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;

public class SetAuthMethodParameters extends PermissionIdParameters {
	@CjRemoteParameter(must=true,usage="权限申请的方式，有：回答问题，密码等")
	ServicewsAuthMethod method;
	public ServicewsAuthMethod getMethod() {
		return method;
	}
	public void setMethod(ServicewsAuthMethod method) {
		this.method = method;
	}
}
