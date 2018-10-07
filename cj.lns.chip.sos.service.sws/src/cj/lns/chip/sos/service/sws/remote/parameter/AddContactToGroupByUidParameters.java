package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AddContactToGroupByUidParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	private String cjtoken;
	@CjRemoteParameter(must = true, usage = "物理组标识，如果gid为负，表明要加入到公共分组中")
	private BigInteger gid;
	@CjRemoteParameter(must = true, usage = "用户物理标识：SosUser表键")
	private BigInteger uid;
	@CjRemoteParameter(must = true, usage = "视窗")
	private BigInteger swsid;
	@CjRemoteParameter(must=false,usage="联系人备注名")
	private String memoName;
	public String getMemoName() {
		return memoName;
	}
	public void setMemoName(String memoName) {
		this.memoName = memoName;
	}
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}

	@Override
	public void cjtoken(String cjtoken) {
		// TODO Auto-generated method stub
		this.cjtoken = cjtoken;
	}

	public BigInteger getGid() {
		return gid;
	}

	public void setGid(BigInteger gid) {
		this.gid = gid;
	}

	public BigInteger getUid() {
		return uid;
	}

	public void setUid(BigInteger uid) {
		this.uid = uid;
	}

	public BigInteger getSwsid() {
		return swsid;
	}

	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}
}
