package cj.lns.chip.sos.website.framework;

import cj.studio.ecm.graph.CircuitException;

/**
 * 提供身份认证策略
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
//authorize授权
public interface IAuthenticator  {
	void authenticate(IAuthForm form) throws AuthenticationException,CircuitException;
}
