package cj.lns.chip.sos.service.user.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GetUserFaceParameters implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="领牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="用户的标识")
	String userCode;
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken=cjtoken;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

}
