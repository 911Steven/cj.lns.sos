package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;

import cj.studio.ecm.graph.CircuitException;

/**
 * 视窗认证服务
 * 
 * <pre>
 * －某个被拒了某项许可的联系人可向视窗持有者或视窗管理员申请许可使用权。
 * </pre>
 * 
 * @author carocean
 *
 */
public interface IAuthPasswordService {

	void setPassword(String who, String password, BigInteger swsid)
			throws CircuitException;

	void removePassword(String who, BigInteger swsid) throws CircuitException;

	/**
	 * 认证
	 * 
	 * <pre>
	 *
	 * </pre>
	 * 
	 * @param passwordId
	 *            密码表标识
	 * @param password
	 *            联系人输入的密码，如果与库中匹配则成功
	 * @return
	 * @throws CircuitException
	 */
	boolean auth(BigInteger passwordId, String password)
			throws CircuitException;

}
