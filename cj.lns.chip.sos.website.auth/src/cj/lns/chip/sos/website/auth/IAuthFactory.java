package cj.lns.chip.sos.website.auth;

import cj.lns.chip.sos.website.framework.IAuthForm;
import cj.lns.chip.sos.website.framework.IAuthenticator;
import cj.studio.ecm.graph.CircuitException;
/**
 * 认证工厂
 * <pre>
 * －识别认证类型，获取认证器
 * </pre>
 * @author carocean
 *
 */
public interface IAuthFactory {
	IAuthenticator authenticator(AccountType type) throws UnknownAuthenticatorException;

	AccountType recognize(IAuthForm form)throws UnknownAuthenticatorException,CircuitException;
}
