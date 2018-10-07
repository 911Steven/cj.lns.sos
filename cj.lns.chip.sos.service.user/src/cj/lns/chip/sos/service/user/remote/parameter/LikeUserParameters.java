package cj.lns.chip.sos.service.user.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class LikeUserParameters implements IRemoteParameters {
	@CjRemoteParameter(must = true, usage = "相似的账号名，请加%号")
	String likeUserCode;
	@CjRemoteParameter(must = true, usage = "翻页当前位置")
	int skip;
	@CjRemoteParameter(must = true, usage = "每页大小")
	int limit;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
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
	public String getLikeUserCode() {
		return likeUserCode;
	}
	public void setLikeUserCode(String likeUserCode) {
		this.likeUserCode = likeUserCode;
	}

}
