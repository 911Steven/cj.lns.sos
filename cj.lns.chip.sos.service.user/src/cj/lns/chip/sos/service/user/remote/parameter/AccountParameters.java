package cj.lns.chip.sos.service.user.remote.parameter;

import cj.lns.chip.sos.service.framework.IRemoteParameters;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteParameter;

public class AccountParameters implements IRemoteParameters{
	@CjRemoteParameter(must = true, usage = "账号名，可以是用户数字编码、视窗、用户邮件、手机等")
	String account;
	@CjRemoteParameter(must = true, usage = "令牌")
	String cjtoken;
	@CjRemoteParameter(must = true, usage = "密码")
	String password;
	@Override
	public String cjtoken() {
		return cjtoken;
	}
	@Override
	public void cjtoken(String cjtoken) {
		this.cjtoken=cjtoken;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
