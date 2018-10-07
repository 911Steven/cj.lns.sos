package cj.lns.chip.sos.service.sws.security.remote;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.security.RoleInfo;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AddRoleSwsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AddUsersParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DeleteRoleParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.RoleParameters;
import cj.lns.chip.sos.service.sws.security.service.IRoleService;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.sws.security.ISecuritySubject;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsRole;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

/**
 * 角色和联系人组是默认的主体，开发者可扩展自己的主体
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "角色服务")
@CjService(name = "/sws/security/role", isExoteric = true)
public class RoleRemote implements IRemoteService, ISecuritySubject {
	@CjServiceRef
	IRoleService roleService;

	@CjRemoteMethod(usage = "查找视窗下的所有角色", returnContentType = "text/json", returnUsage = "返回所有角色List<RoleInfo>")
	public RemoteResult getRoles(RoleParameters parameters)
			throws CircuitException {
		List<SwsRole> list = roleService.getRoles(parameters.getSwsId());
		List<RoleInfo> roles = new ArrayList<RoleInfo>();
		for (SwsRole r : list) {
			RoleInfo ri = new RoleInfo();
			ri.setDescription(r.getDescription());
			ri.setId(r.getId());
			ri.setRoleCode(r.getRoleCode());
			ri.setRoleName(r.getRoleName());
			ri.setSwsId(r.getSwsId());
			roles.add(ri);
		}
		RemoteResult result = new RemoteResult(200, "成功读取持有者用户的视窗");
		String json = new Gson().toJson(roles);
		try {
			result.content().writeBytes(json.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}

	@CjRemoteMethod(usage = "添加一个角色", returnContentType = "text/json", returnUsage = "返回所有角色")
	public RemoteResult addRole(AddRoleSwsParameters p)
			throws CircuitException {
		roleService.addRole(p.getSwsId(), p.getRoleCode(), p.getRoleName(),
				p.getDesc());
		RemoteResult result = new RemoteResult(200, "成功添加");
		return result;
	}

	@CjRemoteMethod(usage = "删除一个角色", returnContentType = "text/json", returnUsage = "返回所有角色")
	public RemoteResult deleteRole(DeleteRoleParameters p)
			throws CircuitException {
		roleService.deleteRole(p.getSwsId(), p.getRoleCode());
		RemoteResult result = new RemoteResult(200, "成功删除");
		return result;
	}

	@CjRemoteMethod(usage = "添加数个用户", returnContentType = "text/json", returnUsage = "返回所有角色")
	public RemoteResult addUsers(AddUsersParameters p) throws CircuitException {
		List<String> users = new ArrayList<String>();
		String[] arr = p.getUserCodes().split(",");
		for (String s : arr) {
			users.add(s);
		}
		roleService.addUsers(p.getSwsId(), p.getRoleCode(), users);
		RemoteResult result = new RemoteResult(200, "OK");
		return result;
	}

	@CjRemoteMethod(usage = "将用户从角色中移除", returnContentType = "text/json", returnUsage = "返回所有角色")
	public RemoteResult removeUsers(AddUsersParameters p)
			throws CircuitException {
		List<String> users = new ArrayList<String>();
		String[] arr = p.getUserCodes().split(",");
		for (String s : arr) {
			users.add(s);
		}
		roleService.removeUsers(p.getSwsId(), p.getRoleCode(), users);
		RemoteResult result = new RemoteResult(200, "ok");
		return result;
	}

	@CjRemoteMethod(usage = "获取指定角色的所有用户", returnContentType = "text/json", returnUsage = "返回内容：List<ContactInfo>")
	public RemoteResult getUsers(DeleteRoleParameters p)
			throws CircuitException {
		List<SwsContact> list = roleService.getContacts(p.getSwsId(),
				p.getRoleCode());
		List<ContactInfo> ret = new ArrayList<ContactInfo>();
		for (SwsContact c : list) {
			ContactInfo ci = new ContactInfo();
			ci.setHeadPic(c.getHeadPic());
			ci.setId(c.getId());
			ci.setMemoName(c.getMemoName());
			ci.setPersonalSignature(c.getPersonalSignature());
			ci.setUserCode(c.getUserCode());
		}
		String json = new Gson().toJson(ret);
		RemoteResult result = new RemoteResult(200, "ok");
		try {
			result.content().writeBytes(json.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}

	@Override
	public String subjectName() {
		return "role";
	}

	@Override
	public boolean containsUser(BigInteger swsId, String roleCode,
			String userCode) {
		// 固定角色定义：allContacts,即所有联系人均属于此角色,但必须：1.userCode是swsId视窗中存在，只要存在则认为包含
		// onlySelf：仅视窗持有者自己，因此判断用户userCode是否是视窗swsId持有者即可
		if ("allContacts".equals(roleCode)) {
			if(roleService.isServicewsOwner(userCode, swsId)){
				return true;
			}
			return roleService.isServicewsContacts(userCode,swsId);
		}
		if ("onlySelf".equals(roleCode)) {
			return roleService.isServicewsOwner(userCode,swsId);
		}
		if ("publicToNet".equals(roleCode)) {
			return true;
		}
		if ("publicToMember".equals(roleCode)) {
			return roleService.isServiceosUser(userCode);
		}
		return roleService.containsUser(swsId, roleCode, userCode);
	}

}
