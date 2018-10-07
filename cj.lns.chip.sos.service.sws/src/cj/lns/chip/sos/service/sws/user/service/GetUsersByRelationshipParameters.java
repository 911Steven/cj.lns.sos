package cj.lns.chip.sos.service.sws.user.service;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GetUsersByRelationshipParameters  implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="领牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="用户代码")
	String userCode;
	@Override
	public String cjtoken() {
		return cjtoken;
	}
	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken=cjtoken;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
}
