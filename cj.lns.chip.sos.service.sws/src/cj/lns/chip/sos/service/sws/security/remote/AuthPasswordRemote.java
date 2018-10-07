package cj.lns.chip.sos.service.sws.security.remote;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.security.service.IAuthPasswordService;
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
@CjRemoteService(usage = "视窗联系人回答问题认证服务。当联系人被拒了某个许可，而视窗设定了申请的方式，则联系人可通过视窗所设策略申请该许可")
@CjService(name = "/sws/security/auth/password", isExoteric = true)
public class AuthPasswordRemote implements IRemoteService {
	@CjServiceRef
	IAuthPasswordService authPasswordService;
}
