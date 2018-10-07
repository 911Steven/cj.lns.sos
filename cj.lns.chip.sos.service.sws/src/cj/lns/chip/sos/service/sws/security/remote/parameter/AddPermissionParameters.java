package cj.lns.chip.sos.service.sws.security.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteContentParameter;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;
import cj.studio.ecm.frame.IFlowContent;

public class AddPermissionParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteContentParameter(must=true,usage="许可对象,由PermissionInfo转换。",chartset="utf-8",dataType="text/json")
	IFlowContent permission;
	
	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken = cjtoken;
	}
	public IFlowContent getPermission() {
		return permission;
	}
	
	
}
