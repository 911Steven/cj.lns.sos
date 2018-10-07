package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AddContatcParameters implements IRemoteParameters {
	@CjRemoteParameter(must=true,usage="令牌")
	private String cjtoken;
	@CjRemoteParameter(must=true,usage="服务视窗标识")
	private BigInteger swsid;
	@CjRemoteParameter(must=true,usage="欲将联系人加入哪个分组标识")
	private BigInteger groupId;
	@CjRemoteParameter(must=true,usage="sos用户标识")
	private String userCode;
	@CjRemoteParameter(must=false,usage="联系人备注名")
	private String memoName;
	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken=cjtoken;
	}

	public String getCjtoken() {
		return cjtoken;
	}

	public void setCjtoken(String cjtoken) {
		this.cjtoken = cjtoken;
	}

	public BigInteger getSwsid() {
		return swsid;
	}

	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}


	public BigInteger getGroupId() {
		return groupId;
	}

	public void setGroupId(BigInteger groupId) {
		this.groupId = groupId;
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
