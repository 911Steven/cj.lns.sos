package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.common.sos.service.model.sws.SwsAuthLogger;

/**
 * 认证日志
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
public interface IAuthLogger {
	void write(SwsAuthLogger logger);
	void remove(BigInteger id);
	List<SwsAuthLogger> getOwnerLoggers(ServicewsAuthMethod method,BigInteger swsid);
	List<SwsAuthLogger> getOwnerLoggers(BigInteger swsid);
	/**
	 * 获取发送者日志，不是视窗持有者
	 * <pre>
	 *
	 * </pre>
	 * @param method
	 * @param sender 发送者
	 * @param permId 什么许可
	 * @param swsid
	 * @return
	 */
	List<SwsAuthLogger> getSenderLoggers(ServicewsAuthMethod method,String sender,BigInteger permId,BigInteger swsid);
	
}
