package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.remote.parameter.CreateQuestionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DelQuestionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.PermissionCodeParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.UpdateQuestionParameters;
import cj.lns.common.sos.service.model.sws.SwsAuthAsk;
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
public interface IAuthAnswerService {
	void addAuthAsk(BigInteger permissionId, BigInteger swsid, String ask,
			String answer) throws CircuitException;

	void removeAuthAsk(BigInteger authAskId) throws CircuitException;

	List<SwsAuthAsk> getAuthAsk(BigInteger permissionId, BigInteger swsid)
			throws CircuitException;
	/**
	 * 
	 * <pre>
	 *
	 * </pre>
	 * @param askId 哪个问题
	 * @param answer 联系人的答案，以此与库中对比看是否匹配
	 * @return
	 */
	boolean auth(BigInteger askId,String answer);

	List<SwsAuthAsk> getAuthAsks(PermissionCodeParameters parameters) throws CircuitException;

	SwsAuthAsk createQuestion(CreateQuestionParameters parameters)throws CircuitException;

	void delQuestion(DelQuestionParameters parameters)throws CircuitException;

	void updateQuestion(UpdateQuestionParameters parameters)throws CircuitException;
}
