package cj.lns.chip.sos.service.sws.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class UserDefaultSwsParameters extends UserFindSwsParameters{
	@CjRemoteParameter(must=true,usage="视窗标识")
	private String swsid;
	public String getSwsid() {
		return swsid;
	}
	public void setSwsid(String swsid) {
		this.swsid = swsid;
	}
}
