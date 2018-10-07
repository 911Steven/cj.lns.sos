package cj.lns.chip.sos.service.sws.security.remote;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AddAuthAskParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AuthParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.CreateQuestionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DelQuestionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.PermissionCodeParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.PermissionIdParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.RemoveAuthAskParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.UpdateQuestionParameters;
import cj.lns.chip.sos.service.sws.security.service.IAuthAnswerService;
import cj.lns.common.sos.service.model.sws.SwsAuthAsk;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

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
@CjService(name = "/sws/security/auth/answer", isExoteric = true)
public class AuthAnswerRemote implements IRemoteService {
	@CjServiceRef
	IAuthAnswerService authAnswerService;
	@CjRemoteMethod(usage = "认证", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "非200即为失败")
	public RemoteResult auth(AuthParameters parameters)
			throws CircuitException {
		boolean pass=authAnswerService.auth(parameters.getAskId(), parameters.getAnswer());
		if(pass){
			return new RemoteResult(200,"认证成功");
		}
		return new RemoteResult(400,"认证失败");
	}
	@CjRemoteMethod(usage = "创建一个问题", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "内容：json")
	public RemoteResult createQuestion(CreateQuestionParameters parameters)
			throws CircuitException {
		SwsAuthAsk ask=authAnswerService.createQuestion(parameters);
		AuthAskInfo ai=new AuthAskInfo();
		ai.setAnswer(ask.getAnswer());
		ai.setAsk(ask.getAsk());
		ai.setId(ask.getId());
		ai.setPermissionId(ask.getPermissionId());
		ai.setSwsId(ask.getSwsId());
		RemoteResult result = new RemoteResult(200, "成功");
		if(ask!=null){
			result.content().writeBytes(new Gson().toJson(ai).getBytes());
		}
		return result;
	}
	@CjRemoteMethod(usage = "删除一个问题", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult delQuestion(DelQuestionParameters parameters)
			throws CircuitException {
		authAnswerService.delQuestion(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "更新一个问题", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult updateQuestion(UpdateQuestionParameters parameters)
			throws CircuitException {
		authAnswerService.updateQuestion(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "添加回答的问题", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult addAuthAsk(AddAuthAskParameters parameters)
			throws CircuitException {
		authAnswerService.addAuthAsk(parameters.getPermissionId(),parameters.getSwsid(), parameters.getAsk(), parameters.getAnswer());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "移除问题", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult removeAuthAsk(RemoveAuthAskParameters parameters)
			throws CircuitException {
		authAnswerService.removeAuthAsk(parameters.getAskId());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "获取指定许可物理标识的问题列表", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "内容：List<QuestionInfo>")
	public RemoteResult getAuthAsk(PermissionIdParameters parameters)
			throws CircuitException {
		List<SwsAuthAsk> list = authAnswerService.getAuthAsk(parameters.getPermissionId(), parameters.getSwsid());
		List<AuthAskInfo> qlist = new ArrayList<AuthAskInfo>();
		for (SwsAuthAsk sq : list) {
			AuthAskInfo qi = new AuthAskInfo();
			qi.setAnswer(sq.getAnswer());
			qi.setPermissionId(sq.getPermissionId());
			qi.setId(sq.getId());
			qi.setAsk(sq.getAsk());
			qi.setSwsId(sq.getSwsId());
			qlist.add(qi);
		}
		String json = new Gson().toJson(qlist);
		RemoteResult result = new RemoteResult(200, "成功");
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "获取指定许可代码的问题列表", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "内容：List<QuestionInfo>")
	public RemoteResult getAuthAsks(PermissionCodeParameters parameters)
			throws CircuitException {
		List<SwsAuthAsk> list = authAnswerService.getAuthAsks(parameters);
		List<AuthAskInfo> qlist = new ArrayList<AuthAskInfo>();
		for (SwsAuthAsk sq : list) {
			AuthAskInfo qi = new AuthAskInfo();
			qi.setAnswer(sq.getAnswer());
			qi.setPermissionId(sq.getPermissionId());
			qi.setId(sq.getId());
			qi.setAsk(sq.getAsk());
			qi.setSwsId(sq.getSwsId());
			qlist.add(qi);
		}
		String json = new Gson().toJson(qlist);
		RemoteResult result = new RemoteResult(200, "成功");
		result.content().writeBytes(json.getBytes());
		return result;
	}
}
