package cj.lns.chip.sos.service.sws.security.remote;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.security.service.IAuthLogger;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;

/**
 * 视窗联系人认证服务
 * 
 * <pre>
 * </pre>
 * 
 * @author carocean
 *
 */
@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "权限审计，指视窗持有者与联系人之间的认证审计日志")
@CjService(name = "/sws/security/logger", isExoteric = true)
public class AuthLoggerRemote implements IRemoteService {
	@CjServiceRef
	IAuthLogger authLogger;
}
