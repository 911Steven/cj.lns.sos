package cj.lns.chip.sos.service.sws.remote.parameter;

import java.math.BigInteger;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class FindServicewsParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = false, usage = "视窗级别，默认为所有视窗类型")
	byte level=-1;
	@CjRemoteParameter(must = true, usage = "分页开始")
	long skip;
	@CjRemoteParameter(must = true, usage = "分页大小")
	long limit;
	@CjRemoteParameter(must = false, usage = "按视窗号查")
	BigInteger swsid;
	@CjRemoteParameter(must = false, usage = "按视窗名模糊查找")
	String swsName;
	@Override
	public String cjtoken() {
		return cjtoken;
	}

	@Override
	public void cjtoken(String token) {
		this.cjtoken = token;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public long getSkip() {
		return skip;
	}

	public void setSkip(long skip) {
		this.skip = skip;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public BigInteger getSwsid() {
		return swsid;
	}

	public void setSwsid(BigInteger swsid) {
		this.swsid = swsid;
	}

	public String getSwsName() {
		return swsName;
	}

	public void setSwsName(String swsName) {
		this.swsName = swsName;
	}
	
}
