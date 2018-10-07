package cj.lns.chip.sos.service.sws.market.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteContentParameter;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;
import cj.studio.ecm.frame.IFlowContent;

public class MountPortletParameters implements IRemoteParameters {

	@CjRemoteContentParameter(chartset = "utf-8", must = true, usage = "要求APP JSON格式", dataType = "json")
	private IFlowContent content;
	@CjRemoteParameter(must = true, usage = "视窗标识")
	BigInteger swsId;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;

	public BigInteger getSwsId() {
		return swsId;
	}

	public void setSwsId(BigInteger swsId) {
		this.swsId = swsId;
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

	public IFlowContent getContent() {
		return content;
	}

	public void setContent(IFlowContent content) {
		this.content = content;
	}
}
