package cj.lns.chip.sos.service.user.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class FindUserParameters implements IRemoteParameters {
	@CjRemoteParameter(must = false, usage = "账号名")
	String userCode;
	@CjRemoteParameter(must = false, usage = "在线：on|off")
	String online;
	@CjRemoteParameter(must = false, usage = "性别：-1无设置，1female|0male，默认为-1")
	int sex=-1;
	@CjRemoteParameter(must = true, usage = "分页查询开始位置")
	int start;
	@CjRemoteParameter(must = true, usage = "每次取最大数")
	int max;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getCjtoken() {
		return cjtoken;
	}
	public void setCjtoken(String cjtoken) {
		this.cjtoken = cjtoken;
	}
	
}
