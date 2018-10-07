package cj.lns.chip.sos.service.sws.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class ContactGroupParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	private String cjtoken;
	@CjRemoteParameter(must=true,usage="组名")
	private String groupName;
	@CjRemoteParameter(must=true,usage="视窗")
	private String swsid;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSwsid() {
		return swsid;
	}

	public void setSwsid(String swsid) {
		this.swsid = swsid;
	}

}
