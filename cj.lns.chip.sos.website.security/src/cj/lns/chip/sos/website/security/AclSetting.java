package cj.lns.chip.sos.website.security;

import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

public class AclSetting implements IAclSetting {
	SecurityCenter securityCenter;
	ISecurityObjectProvider provider;
	IPaService pa;
	public AclSetting(SecurityCenter securityCenter,
			ISecurityObjectProvider p,IPaService pa) {
		this.securityCenter=securityCenter;
		this.provider=p;
		this.pa=pa;
	}
	@Override
	public Acl getAcl(String swsid, String resourceId, String valueId,
			String permCode) throws CircuitException {
		Frame frame = new Frame("getObjectAcl /sws/security/permission sos/1.0");
		frame.parameter("swsId", swsid);
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if("404".equals(back.head("status"))){
			return null;
		}
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		Acl acl=new Gson().fromJson(json, Acl.class);
		return acl;
	}
	@Override
	public void allowRole(String resourceId, String valueId, String permCode,
			String permName, String roleCode, String swsId)
					throws CircuitException {
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		pa.allowRole(resourceId, valueId, permCode, permName, roleCode, swsId);
	}
	@Override
	public void allowContactGroup(String groupId, String resourceId,
			String valueId, String permCode, String permName, String swsId)
					throws CircuitException {
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		pa.allowContactGroup(groupId, resourceId, valueId, permCode, permName, swsId);
	}
	@Override
	public void denyContactGroup(String groupId, String resourceId,
			String valueId, String permCode, String swsId)
					throws CircuitException {
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		pa.denyContactGroup(groupId, resourceId, valueId, permCode, swsId);
	}
	@Override
	public void allowContacts(String users, String resourceId, String valueId,
			String permCode, String permName, String swsId)
					throws CircuitException {
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		pa.allowContacts(users, resourceId, valueId, permCode, permName, swsId);
	}
	@Override
	public void denyContacts(String users, String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException {
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		pa.getDenyContacts(resourceId, valueId, permCode, swsId);
	}
}
