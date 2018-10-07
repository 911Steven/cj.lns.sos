package cj.lns.chip.sos.website.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cj.lns.chip.sos.website.auth.AccountType;
import cj.lns.chip.sos.website.auth.IAuthFactory;
import cj.lns.chip.sos.website.auth.UnknownAuthenticatorException;
import cj.lns.chip.sos.website.framework.IAuthForm;
import cj.lns.chip.sos.website.framework.IAuthenticator;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjMethod;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjService(name = "authFactory", constructor = "get")
public class AuthFactory implements IAuthFactory, IServiceAfter {
	private static IAuthFactory factory;
	private Map<String, IAuthenticator> auths;

	@CjMethod(returnDefinitionId = ".")
	public static IAuthFactory get() {
		if (factory == null) {
			factory = new AuthFactory();
		}
		return factory;
	}

	@Override
	public void onAfter(IServiceSite site) {
		auths = new HashMap<>();
		ServiceCollection<IAuthenticator> col = site
				.getServices(IAuthenticator.class);
		for (IAuthenticator a : col) {
			CjService cs = a.getClass().getAnnotation(CjService.class);
			if (cs == null) {
				continue;
			}
			auths.put(cs.name(), a);
		}
	}

	@Override
	public IAuthenticator authenticator(AccountType type)
			throws UnknownAuthenticatorException {
		String name = String.format("%sAuth", type.name());
		if (!auths.containsKey(name)) {
			throw new UnknownAuthenticatorException("404",
					String.format("不认识的认证类型：%s", name));
		}
		return auths.get(name);
	}

	@Override
	public AccountType recognize(IAuthForm form)
			throws UnknownAuthenticatorException, CircuitException {
		String account = (String)form.get("account");
		Pattern pattern = Pattern.compile("[0-9]+");
		boolean isNum = pattern.matcher(account).matches();
		if (isNum) {
			int c=Integer.valueOf(Character.toString(account.charAt(0)));
			if ((c>=1&&c<2)||(c >= 6 && c <= 9)) {// 为视窗
				form.authType(AccountType.servicewsNum.name());
				return AccountType.servicewsNum;
			}
			if (c >= 2 && c <= 5) {// 为用户数字编码
				form.authType(AccountType.userNum.name());
				return AccountType.userNum;
			}
			if (c ==1) {// 可能为手机号，将来此处罗列出手机编号
				form.authType(AccountType.cellphone.name());
				return AccountType.cellphone;
			}
		} else {
			if(account.contains("@")){
				form.authType(AccountType.email.name());
				return AccountType.email;
			}else{
				form.authType(AccountType.custom.name());
				return AccountType.custom;
			}
		}
		throw new UnknownAuthenticatorException("600", String.format("不能识别的账户号码:%s", account));
	}

}
