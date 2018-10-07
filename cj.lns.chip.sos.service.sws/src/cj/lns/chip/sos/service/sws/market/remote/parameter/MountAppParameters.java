package cj.lns.chip.sos.service.sws.market.remote.parameter;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteContentParameter;
import cj.studio.ecm.frame.IFlowContent;

public class MountAppParameters extends AppParameters{
	@CjRemoteContentParameter(chartset="utf-8",must=true,usage="要求APP JSON格式", dataType = "json")
	private IFlowContent content;
	public IFlowContent getContent() {
		return content;
	}
	public void setContent(IFlowContent content) {
		this.content = content;
	}
}
