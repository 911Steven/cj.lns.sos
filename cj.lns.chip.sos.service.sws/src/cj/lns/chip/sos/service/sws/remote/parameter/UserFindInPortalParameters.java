package cj.lns.chip.sos.service.sws.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class UserFindInPortalParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "用户统一标识")
	String userCode;
	@CjRemoteParameter(must = true, usage = "框架标识")
	String portalId;
	public UserFindInPortalParameters() {
		// TODO Auto-generated constructor stub
	}
	public String getPortalId() {
		return portalId;
	}
	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String token) {
		// TODO Auto-generated method stub
		this.cjtoken = token;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
}