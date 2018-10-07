package cj.lns.chip.sos.service.framework.remote;

import java.math.BigInteger;
import java.util.List;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.framework.remote.parameter.AllPortalsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.CreateServicewsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.CreateSuperswsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.DelSuperswsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.ExistsSwsTemplateInPortalParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.FindPortalInfoParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.FrameworkReporterParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GetAllSuperswsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GetAllSwsTemplateParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.SetUserRegisterDefaultTemplateParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.getSosVersionParameters;
import cj.lns.chip.sos.service.framework.service.IFrameworkService;
import cj.lns.chip.sos.service.framework.service.ISwsTemplateService;
import cj.lns.chip.sos.service.portal.PortalInfo;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "框架服务")
@CjService(name = "/framework/info/", isExoteric = true)
public class FrameworkRemote implements IRemoteService {
	@CjServiceRef(refByName = "frameworkService")
	private IFrameworkService framework;
	@CjServiceRef
	private ISwsTemplateService swsTemplateService;
	public FrameworkRemote() {
	}
	@CjRemoteMethod(usage = "获取服务操作系统版本信息", returnContentType = "text/json", returnUsage = "返回list json")
	public RemoteResult getSosVersion(getSosVersionParameters parameters)throws CircuitException{
		List<?> version=framework.getSosVersion();
		RemoteResult result = new RemoteResult(200, "成功");
		String json=new Gson().toJson(version);
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "获取所有超级视窗", returnContentType = "text/json", returnUsage = "返回：list")
	public RemoteResult getAllSupersws(GetAllSuperswsParameters parameters)
			throws CircuitException {
		List<SwsInfo> list=swsTemplateService.getAllSupersws();
		RemoteResult result = new RemoteResult(200, "成功");
		String json=new Gson().toJson(list);
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "删除超级视窗", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult delSupersws(DelSuperswsParameters parameters)
			throws CircuitException {
		swsTemplateService.delSupersws(parameters.getPortalId());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "创建超级视窗", returnContentType = "text/json", returnUsage = "return head('swsid')")
	public RemoteResult createSupersws(CreateSuperswsParameters parameters)
			throws CircuitException {
		BigInteger swsid=swsTemplateService.createSupersws(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		result.head("swsid",swsid.toString());
		return result;
	}
	@CjRemoteMethod(usage = "获取所有视窗模板", returnContentType = "text/json", returnUsage = "返回视窗模板")
	public RemoteResult getAllSwsTemplate(GetAllSwsTemplateParameters parameters)
			throws CircuitException {
		List<ServicewsInfo> list=swsTemplateService.getAllSwsTemplate(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		String json=new Gson().toJson(list);
		result.content().writeBytes(json.getBytes());
		return result;
	}
	
	/*
	 * 视窗模板的定义位置：
	 * －概念：只要是赋给portal的视窗实例均作用视窗模板，用于在用户关注此框架时分配新视窗
	 * －每个框架一个
	 * －用户注册时指定默认模板
	 */
	@CjRemoteMethod(usage = "创建一个空的服务视窗，基视窗的继承为-1，一个portal有且只有一个视窗模板，且必须至少有一个", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult createServicews(CreateServicewsParameters parameters)
			throws CircuitException {
		framework.createServicews(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "判断在框架下是否有视窗模板", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult existsSwsTemplateInPortal(
			ExistsSwsTemplateInPortalParameters parameters)
			throws CircuitException {
		boolean exists = framework.existsSwsTemplateInPortal(parameters);
		RemoteResult result = null;
		if (exists) {
			result = new RemoteResult(200, "存在");
		} else {
			result = new RemoteResult(404, "不存在");
		}
		return result;
	}

	@CjRemoteMethod(usage = "查询一个框架信息", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult findPortalInfo(FindPortalInfoParameters parameters)
			throws CircuitException {
		PortalInfo pi=framework.findPortalInfo(parameters);
		RemoteResult result = new RemoteResult(200, "成功读取持有者户默认的的视窗");
		String json = new Gson().toJson(pi);
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjRemoteMethod(usage = "获取服务操作系统中所有框架", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult getPortals(AllPortalsParameters parameters)
			throws CircuitException {
		List<PortalInfo> list=framework.getPortals();
		RemoteResult result = new RemoteResult(200, "成功读取持有者户默认的的视窗");
		String json=new Gson().toJson(list);
		result.content().writeBytes(json.getBytes());
		return result;
	}

	/**
	 * 用户注册时会列出框架，其中默认模板关联的框架为默认框架，注册后便以此视窗模板实例化
	 * 
	 * <pre>
	 *
	 * </pre>
	 * 
	 * @param parameters
	 * @return
	 * @throws CircuitException
	 */
	@CjRemoteMethod(usage = "设置在用户注册时默认选择的视窗模板", returnContentType = "text/json", returnUsage = "返回视窗")
	public RemoteResult setWhenUserRegisterDefaultTemplate(
			SetUserRegisterDefaultTemplateParameters parameters) throws CircuitException {
		framework.setUserRegisterDefaultTemplate(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "将本地框架信息报告到服务中心", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult report(FrameworkReporterParameters parameters)
			throws CircuitException {
		framework.report(parameters);
		RemoteResult result = new RemoteResult(200, "成功报送本地website服务框架");
		return result;
	}

}
