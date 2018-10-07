package cj.lns.chip.sos.service.sws.security.remote;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.security.PaInfo;
import cj.lns.chip.sos.service.sws.security.PermissionInfo;
import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AddPermissionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowRoleParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyGroupParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.FindPermissionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetObjectAclParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetObjectAclsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.ObjectAllPermissionsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.ObjectPermissionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.PermissionIdParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.SetAuthMethodParameters;
import cj.lns.chip.sos.service.sws.security.service.IPermissionService;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsPa;
import cj.lns.common.sos.service.model.sws.SwsPd;
import cj.lns.common.sos.service.model.sws.SwsPermission;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

/**
 * 授权服务
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "授权服务")
@CjService(name = "/sws/security/permission", isExoteric = true)
public class PermissionRemote implements IRemoteService {
	@CjServiceRef(refByName = "permissionService")
	private IPermissionService permission;

	private SwsPermission toEntity(PermissionInfo pi) {
		SwsPermission p = new SwsPermission();
		p.setObjectInst(pi.getObjectInst());
		p.setObjectName(pi.getObjectName());
		p.setPermissionCode(pi.getPermissionCode());
		p.setAuthMethod(pi.getAuthMethod().getValue());
		p.setSwsId(pi.getSwsId());
		return p;
	}

	private PermissionInfo toInfo(SwsPermission pi) {
		PermissionInfo p = new PermissionInfo();
		p.setObjectInst(pi.getObjectInst());
		p.setPermissionId(pi.getId());
		p.setObjectName(pi.getObjectName());
		p.setPermissionCode(pi.getPermissionCode());
		p.setAuthMethod(ServicewsAuthMethod.valueOf(pi.getAuthMethod()));
		p.setSwsId(pi.getSwsId());
		return p;
	}
	@CjRemoteMethod(usage = "获取某项资源的访问控制列表，用于对该资源的授权", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "内容：ACL。代码：404 许可不存在")
	public RemoteResult getObjectAcl(
			GetObjectAclParameters p) throws CircuitException {
		Acl acl = permission.getObjectAcl(p.getSwsId(),
				 p.getObjectName(), p.getObjectInst(),p.getPermissionCode());
		if(acl==null){
			return new RemoteResult(404,"许可不存在");
		}
		RemoteResult result = new RemoteResult(200, "成功");
		String json = new Gson().toJson(acl);
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "获取指定联系人在某类资源下具有给定许可的每项资源的访问控制列表，持有者除外", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "内容：List<PermissionInfo>")
	public RemoteResult getObjectAcls(
			GetObjectAclsParameters p) throws CircuitException {
		List<Acl> list = permission.getObjectAcls(p);
		RemoteResult result = new RemoteResult(200, "成功");
		String json = new Gson().toJson(list);
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "获取指定联系人在资源对象上的所有许可，持有者除外", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "内容：List<PermissionInfo>")
	public RemoteResult getObjectAllPermissions(
			ObjectAllPermissionsParameters p) throws CircuitException {
		List<SwsPermission> list = permission.getUserPermissions(p.getSwsid(),
				p.getUserCode(), p.getObjectName(), p.getObjectInst());
		List<PermissionInfo> retList = new ArrayList<PermissionInfo>();
		for (SwsPermission sp : list) {
			PermissionInfo e = toInfo(sp);
			retList.add(e);
		}
		RemoteResult result = new RemoteResult(200, "成功");
		String json = new Gson().toJson(retList);
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjRemoteMethod(usage = "获取指定联系人在资源对象的指定许可，持有者除外", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "200 许可可用，201 许可被拒绝, 202许可不存在，内容json对象：许可")
	public RemoteResult getObjectPermission(ObjectPermissionParameters p)
			throws CircuitException {
		SwsPermission sp = permission.getUserPermissions(p.getSwsid(),
				p.getUserCode(), p.getObjectName(), p.getObjectInst(),
				p.getPermissionCode());
		if(sp==null){
			sp=permission.find(p.getSwsid(), p.getObjectName(), p.getObjectInst(), p.getPermissionCode());
			if(sp==null){
				RemoteResult result = new RemoteResult(202, "许可不存在");
				return result;
			}
			RemoteResult result = new RemoteResult(201, "许可存在但被拒");
			PermissionInfo e = toInfo(sp);
			String json = new Gson().toJson(e);
			result.content().writeBytes(json.getBytes());
			return result;
		}
		PermissionInfo e = toInfo(sp);
		RemoteResult result = new RemoteResult(200, "许可可用");
		String json = new Gson().toJson(e);
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjRemoteMethod(usage = "添加许可", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult addPermission(AddPermissionParameters parameters)
			throws CircuitException {
		byte[] b = parameters.getPermission().readFully();
		PermissionInfo pi = new Gson().fromJson(new String(b),
				PermissionInfo.class);
		SwsPermission perm = toEntity(pi);
		permission.addPermission(perm);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "添加许可", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult removePermission(PermissionIdParameters parameters)
			throws CircuitException {
		permission.removePermission(parameters.getPermissionId());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "添除许可的充许和拒绝项", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult clearPermission(PermissionIdParameters parameters)
			throws CircuitException {
		permission.clearPermission(parameters.getPermissionId());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "查找一个许可", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult findPermission(FindPermissionParameters parameters)
			throws CircuitException {
		SwsPermission p = permission.find(parameters.getSwsId(),
				parameters.getObjectName(), parameters.getObjectInst(),
				parameters.getPermissionCode());
		RemoteResult result = new RemoteResult(200, "成功");
		String json = new Gson().toJson(p);
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "充许一个角色主体，如果许可不存在则新建", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult allowRole(AllowRoleParameters parameters)
			throws CircuitException {
		permission.allowRole(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "拒绝一个联系人组主体", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult denyGroup(DenyGroupParameters parameters)
			throws CircuitException {
		permission.denyGroup(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	
	@CjRemoteMethod(usage = "充许一个联系人组主体，如果许可不存在则新建", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult allowGroup(AllowGroupParameters parameters)
			throws CircuitException {
		permission.allowGroup(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "充许一组用户对资源的许可，如果许可不存在则新建", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult allowContacts(AllowContactsParameters parameters)
			throws CircuitException {
		permission.allowContacts(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "拒绝一组用户对资源的许可，如果许可不存在则新建", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult denyContacts(DenyContactsParameters parameters)
			throws CircuitException {
		permission.denyContacts(parameters);
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}
	@CjRemoteMethod(usage = "向许可添加充许的主体", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult allow(AllowParameters parameters)
			throws CircuitException {
		permission.allow(parameters.getPermissionId(),
				parameters.getSubjectName(), parameters.getSubjectInst());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "移除指定许可的主体", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult removeAllow(AllowParameters parameters)
			throws CircuitException {
		permission.removeAllow(parameters.getPermissionId(),
				parameters.getSubjectName(), parameters.getSubjectInst());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "清除指定许可的所有主体", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult clearAllows(PermissionIdParameters parameters)
			throws CircuitException {
		permission.clearAllows(parameters.getPermissionId());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "拒绝用户使用指定许可", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult deny(DenyParameters parameters)
			throws CircuitException {
		permission.deny(parameters.getPermissionId(), parameters.getUserCode());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "判断用户是否被拒绝使用指定许可", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult isDeny(DenyParameters parameters)
			throws CircuitException {
		permission.isDeny(parameters.getPermissionId(),
				parameters.getUserCode());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "将用户从被拒绝使用指定许可名单中移除", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult removeDeny(DenyParameters parameters)
			throws CircuitException {
		permission.removeDeny(parameters.getPermissionId(),
				parameters.getUserCode());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "清空指定许可的拒绝表", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult clearDenies(PermissionIdParameters parameters)
			throws CircuitException {
		permission.clearDenies(parameters.getPermissionId());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

	@CjRemoteMethod(usage = "获取拒绝表", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "返回被拒绝的用户标识，在head中key:user-codes，以,号隔开")
	public RemoteResult getDenies(PermissionIdParameters parameters)
			throws CircuitException {
		List<SwsPd> list = permission.getDenies(parameters.getPermissionId());
		String users = "";
		for (SwsPd p : list) {
			users += String.format("%s,", p.getUserCode());
		}
		RemoteResult result = new RemoteResult(200, "成功");
		result.head("user-codes", users);
		return result;
	}

	@CjRemoteMethod(usage = "获取充许表", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "返回内容SwsPaInfo")
	public RemoteResult getAllows(PermissionIdParameters parameters)
			throws CircuitException {
		List<SwsPa> list = permission.getAllows(parameters.getPermissionId());
		List<PaInfo> ret = new ArrayList<PaInfo>();
		for (SwsPa p : list) {
			PaInfo pi = new PaInfo();
			pi.setSubjectInst(p.getSubjectInst());
			pi.setSubjectName(p.getSubjectName());
			ret.add(pi);
		}
		String json = new Gson().toJson(ret);
		RemoteResult result = new RemoteResult(200, "成功");
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "获取充许的联系人", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "返回内容json:List<ContactInfo>")
	public RemoteResult getAllowContacts(GetContactsParameters parameters)
			throws CircuitException {
		List<SwsContact> list = permission.getAllowContacts(parameters);
		List<ContactInfo> users=new ArrayList<>();
		for(SwsContact c:list){
			ContactInfo ci=new ContactInfo();
			ci.setHeadPic(c.getHeadPic());
			ci.setId(c.getId());
			ci.setMemoName(c.getMemoName());
			ci.setOwnerGroupId(c.getOwnerGroupId());
			ci.setPersonalSignature(c.getPersonalSignature());
			ci.setUserCode(c.getUserCode());
			users.add(ci);
		}
		String json = new Gson().toJson(users);
		RemoteResult result = new RemoteResult(200, "成功");
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "获取拒绝的联系人", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "返回内容json:List<ContactInfo>")
	public RemoteResult getDenyContacts(GetContactsParameters parameters)
			throws CircuitException {
		List<SwsContact> list = permission.getDenyContacts(parameters);
		List<ContactInfo> users=new ArrayList<>();
		for(SwsContact c:list){
			ContactInfo ci=new ContactInfo();
			ci.setHeadPic(c.getHeadPic());
			ci.setId(c.getId());
			ci.setMemoName(c.getMemoName());
			ci.setOwnerGroupId(c.getOwnerGroupId());
			ci.setPersonalSignature(c.getPersonalSignature());
			ci.setUserCode(c.getUserCode());
			users.add(ci);
		}
		String json = new Gson().toJson(users);
		RemoteResult result = new RemoteResult(200, "成功");
		result.content().writeBytes(json.getBytes());
		return result;
	}
	@CjRemoteMethod(usage = "清空指定许可的拒绝表", returnChartset = "utf-8", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult setAuthMethod(SetAuthMethodParameters parameters)
			throws CircuitException {
		permission.setAuthMethod(parameters.getPermissionId(),
				parameters.getMethod());
		RemoteResult result = new RemoteResult(200, "成功");
		return result;
	}

}
