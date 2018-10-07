package cj.lns.chip.sos.service.framework.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteContentParameter;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;
import cj.studio.ecm.frame.IFlowContent;

public class FrameworkReporterParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "用户统一标识，具有框架报告权限的用户")
	String userCode;
	@CjRemoteContentParameter(chartset="utf-8",dataType="json",must=true,usage="内容是FrameworkInfo的json数据")
	IFlowContent content;
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
	
	public IFlowContent getContent() {
		return content;
	}
	public void setContent(IFlowContent content) {
		this.content = content;
	}
}
