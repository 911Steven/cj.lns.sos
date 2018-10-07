package cj.lns.chip.sos.service.user.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class UserProfileParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "账号名")
	String userCode;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "昵称")
	String nickName;
	@CjRemoteParameter(must = true, usage = "真实名")
	String realName;
	@CjRemoteParameter(must = true, usage = "头像")
	String headUrl;
	@CjRemoteParameter(must = true, usage = "邮件")
	String email;
	@CjRemoteParameter(must = true, usage = "电话")
	String cellphone;
	@CjRemoteParameter(must = true, usage = "籍惯")
	String jiguan;
	@CjRemoteParameter(must = true, usage = "居住地")
	String jizhudi;
	@Override
	public String cjtoken() {
		// TODO Auto-generated method stub
		return cjtoken;
	}
	@Override
	public void cjtoken(String token) {
		// TODO Auto-generated method stub
		this.cjtoken=token;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCellphone() {
		return cellphone;
	}
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
	public String getJiguan() {
		return jiguan;
	}
	public void setJiguan(String jiguan) {
		this.jiguan = jiguan;
	}
	public String getJizhudi() {
		return jizhudi;
	}
	public void setJizhudi(String jizhudi) {
		this.jizhudi = jizhudi;
	}
	
}
