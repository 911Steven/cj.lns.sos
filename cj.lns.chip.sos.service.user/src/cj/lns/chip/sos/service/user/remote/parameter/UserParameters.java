package cj.lns.chip.sos.service.user.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class UserParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "账号名")
	String userCode;
	@CjRemoteParameter(must = false, usage = "昵称")
	String nickName;
	@CjRemoteParameter(must = true, usage = "性别：male,female")
	String sex;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = false, usage = "网盘空间大小，默认为不限制")
	long capacity=-1;
	@CjRemoteParameter(must = false, usage = "主存空间大小，默认为不限制")
	long homeSize=-1;
	@CjRemoteParameter(must = true, usage = "密码")
	String password;
//	@CjRemoteParameter(must = true, usage = "关注的框架集，以;号分隔开")
//	String portalIds;
//	@CjRemoteParameter(must = false, usage = "默认框架，登录后直进，如果没有则每次登录时列出让用户选择")
//	String defaultPortalId;
	@CjRemoteParameter(must = false, usage = "邮箱")
	String email;
	@CjRemoteParameter(must = false, usage = "电话号码")
	String cellphone;
	@CjRemoteParameter(must = false, usage = "头像图片地址")
	String faceImg;
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getFaceImg() {
		return faceImg;
	}public void setFaceImg(String faceImg) {
		this.faceImg = faceImg;
	}
	public long getCapacity() {
		return capacity;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
	public long getHomeSize() {
		return homeSize;
	}
	public void setHomeSize(long homeSize) {
		this.homeSize = homeSize;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
//	public String getPortalIds() {
//		return portalIds;
//	}
//	public void setPortalIds(String portalIds) {
//		this.portalIds = portalIds;
//	}
//	public String getDefaultPortalId() {
//		return defaultPortalId;
//	}
//	public void setDefaultPortalId(String defaultPortalId) {
//		this.defaultPortalId = defaultPortalId;
//	}
	
}
