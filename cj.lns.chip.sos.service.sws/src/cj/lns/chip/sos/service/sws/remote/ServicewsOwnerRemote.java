package cj.lns.chip.sos.service.sws.remote;

import java.util.List;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.service.sws.remote.parameter.GetOwnerParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.UserDefaultSwsParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.UserFindSwsParameters;
import cj.lns.chip.sos.service.sws.user.service.ISwsOwnerService;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗持有者服务")
@CjService(name = "/sws/owner", isExoteric = true)
public class ServicewsOwnerRemote implements IRemoteService {
	@CjServiceRef
	ISwsOwnerService swsOwnerService;
	@CjRemoteMethod(usage = "返回用户的视窗列表", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult getAllSws(UserFindSwsParameters parameters)
			throws CircuitException {
		SosUserInfo user = swsOwnerService.getAllSws(parameters.getUserCode());
		Gson g = new Gson();
		RemoteResult result = new RemoteResult(200, "返回用户的视窗列表");
		result.content().writeBytes(g.toJson(user).getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "返回用户的视窗号列表", returnContentType = "text/json", returnUsage = "list json")
	public RemoteResult getAllSwsid(UserFindSwsParameters parameters)
			throws CircuitException {
		List<String> swi = swsOwnerService.getAllSwsid(parameters.getUserCode());
		Gson g = new Gson();
		RemoteResult result = new RemoteResult(200, "返回用户的视窗列表");
		result.content().writeBytes(g.toJson(swi).getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "返回视窗的持有人信息", returnContentType = "text/json", returnUsage = "内容jsonObject")
	public RemoteResult getOwnerInfo(GetOwnerParameters parameters)
			throws CircuitException {
		SosUserInfo owner= swsOwnerService.getOwnerInfo(parameters);
		Gson g = new Gson();
		RemoteResult result = new RemoteResult(200, "返回用户的视窗列表");
		result.content().writeBytes(g.toJson(owner).getBytes());
		return result;
	}
	//在user表中字段defaultSws为默认视窗
	@CjRemoteMethod(usage = "查找用户的默认视窗，即当前用户登录后进入的视窗，如果没有则返回405异常", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult findDefaultSws(UserFindSwsParameters parameters)
			throws CircuitException {
		SosUserInfo swi = swsOwnerService.getServicews(parameters.getUserCode());
		Gson g = new Gson();
		RemoteResult result = new RemoteResult(200, "成功读取持有者户默认的的视窗");
		result.content().writeBytes(g.toJson(swi).getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "将指定视窗设为持有者默认视窗", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult setDefaultSws(UserDefaultSwsParameters parameters)
			throws CircuitException {
		swsOwnerService.setDefaultSws(parameters.getUserCode(),parameters.getSwsid());
		RemoteResult result = new RemoteResult(200, "成功将指定视窗设为持有者默认视窗");
		return result;
	}
	
}
