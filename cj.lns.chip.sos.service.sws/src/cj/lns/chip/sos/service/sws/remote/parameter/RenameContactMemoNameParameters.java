package cj.lns.chip.sos.service.sws.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class RenameContactMemoNameParameters implements IRemoteParameters{
	@CjRemoteParameter(must=true,usage="令牌")
	private String cjtoken;
	@CjRemoteParameter(must=true,usage="视窗标识")
	private long swsid;
	@CjRemoteParameter(must=true,usage="统一用户标识")
	private String userCode;
	@CjRemoteParameter(must=true,usage="要变更的备注名")
	private String memoName;
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

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getMemoName() {
		return memoName;
	}

	public void setMemoName(String memoName) {
		this.memoName = memoName;
	}
	
}
