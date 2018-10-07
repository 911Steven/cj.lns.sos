package cj.lns.chip.sos.website.security;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

public class AclFinder implements IAclFinder {
	SecurityCenter center;
	ISecurityObjectProvider provider;
	public AclFinder(SecurityCenter securityCenter, ISecurityObjectProvider p) {
		center = securityCenter;
		provider = p;
	}

	@Override
	public List<Acl> getAcls(String resourceId, String permCode,
			IServicewsContext sws) throws CircuitException {
		Frame frame = new Frame("getObjectAcls /sws/security/permission sos/1.0");
		frame.parameter("swsId", sws.swsid());
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		frame.parameter("objectName", resourceId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("principal",sws.visitor().principal());
		
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if("404".equals(back.head("status"))){
			return new ArrayList<Acl>();
		}
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		List<Acl> acl=new Gson().fromJson(json,new TypeToken<List<Acl>>(){}.getType());
		return acl;
	}

	@Override
	public boolean hasPermission(String resourceId, String valueId,
			String permCode, IServicewsContext sws) throws CircuitException {
		Frame frame = new Frame("getObjectPermission /sws/security/permission sos/1.0");
		frame.parameter("swsid", sws.swsid());
		if(StringUtil.isEmpty(resourceId)){
			resourceId=ISecurityObject.securityObject(provider.root()).resourceId();
		}
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst",valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("userCode",sws.visitor().principal());
		
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if("202".equals(back.head("status"))){
			return false;
		}
		if("201".equals(back.head("status"))){
			return false;
		}
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		return true;
	}

	@Override
	public List<Acl> getAcls(String resourceId, String[] permCodes,
			IServicewsContext sws) throws CircuitException {
		String format="";
		for(String s:permCodes){
			format+=String.format("%s,", s);
		}
		return getAcls(resourceId, format, sws);
	}


	

	
}
