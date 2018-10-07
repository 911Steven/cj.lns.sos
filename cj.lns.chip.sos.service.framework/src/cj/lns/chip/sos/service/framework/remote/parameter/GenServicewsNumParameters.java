package cj.lns.chip.sos.service.framework.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class GenServicewsNumParameters implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="用户已分配的前缀号码")
	int assignedNum;
	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken=cjtoken;
	}
	public int getAssignedNum() {
		return assignedNum;
	}
	public void setAssignedNum(int assignedNum) {
		this.assignedNum = assignedNum;
	}
}
