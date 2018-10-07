package cj.lns.chip.sos.service.framework.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class SetUserRegisterDefaultTemplateParameters implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="令牌")
	String cjtoken;
	@CjRemoteParameter(must=true,usage="视窗标识")
	long swsid;
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
	public long getSwsid() {
		return swsid;
	}
	public void setSwsid(long swsid) {
		this.swsid = swsid;
	}
}
