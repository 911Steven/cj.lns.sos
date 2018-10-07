package cj.lns.chip.sos.website.sws.service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.service.sws.security.PermissionInfo;
import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "securityService")
public class SecurityService implements ISecurityService {

	@Override
	public boolean  isOwner(String principal, String swsid) throws CircuitException {
		Frame f = new Frame(
				"isOwner /sws/instance sos/1.0");
		f.parameter("swsid",swsid);
		f.parameter("userCode",principal);
		Circuit c = new Circuit("sos/1.0 200 OK");
//		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");// 等待15秒
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(f, c);
		if ("frame/bin".equals(c.contentType())) {
			Frame frame = new Frame(c.content().readFully());
			if ("404".equals(frame.head("status"))) {
				return false;
			}
			if (!"200".equals(frame.head("status"))) {
				throw new CircuitException(frame.head("status"), String.format(
						"在远程服务器上出现错误。原因：%s", frame.head("message")));
			}
			return true;
		}
		return false;
	}
	@Override
	public Map<String, Object>  getSws(String swsid) throws CircuitException {
		Frame f = new Frame(
				"getServicewsSummary /sws/instance sos/1.0");
		f.parameter("swsid",swsid);
		Circuit c = new Circuit("sos/1.0 200 OK");
//		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");// 等待15秒
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(f, c);
		if ("frame/bin".equals(c.contentType())) {
			Frame frame = new Frame(c.content().readFully());
			if (!"200".equals(frame.head("status"))) {
				throw new CircuitException(frame.head("status"), String.format(
						"在远程服务器上出现错误。原因：%s", frame.head("message")));
			}
			String json=new String(frame.content().readFully());
			return new Gson().fromJson(json, new TypeToken<HashMap<String,Object>>(){}.getType());
		}
		return new HashMap<>();
	}
	@Override
	public int hasSwsVisitPermission(String principal, String swsid,Map<String, Object> fillSws) throws CircuitException {
		Map<String, Object> sws=getSws(swsid);
		fillSws.putAll(sws);
		Map<String, Object> owner=(Map<String,Object>)sws.get("owner");
		if(!sws.isEmpty()&&principal.equals(owner.get("userCode"))){//如果是持有人则拥有全部权限
			sws=null;
			PermissionInfo pi=new PermissionInfo();
			pi.setAuthMethod(ServicewsAuthMethod.none);
			pi.setDirObject(true);
			pi.setObjectInst(swsid);
			pi.setObjectName("servicews");
			pi.setPermissionCode("visit");
			pi.setSwsId(new BigInteger(swsid));
			fillSws.put("permission", pi);
			return 200;
		}
		Frame f = new Frame(
				"getObjectPermission /sws/security/permission sos/1.0");
		f.contentType("json");
		f.contentChartset("utf-8");
		f.parameter("permissionCode","visit");
		f.parameter("swsid",swsid);
		f.parameter("userCode",principal);
		f.parameter("objectName","servicews");
		f.parameter("objectInst","the.servicews");//这是固定的，the.servicews表示当前自己的视窗
		Circuit c = new Circuit("sos/1.0 200 OK");
//		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");// 等待15秒
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(f, c);
		if ("frame/bin".equals(c.contentType())) {
			Frame frame = new Frame(c.content().readFully());
			int status=Integer.valueOf(frame.head("status"));
			if (status<200||status>202) {
				throw new CircuitException(frame.head("status"), String.format(
						"在远程服务器上出现错误。原因：%s", frame.head("message")));
			}
			if(status==202){
				return status;
			}
			if(frame.content().readableBytes()<1){
				return 202;
			}
			String json=new String(frame.content().readFully());
			PermissionInfo pi=new Gson().fromJson(json, PermissionInfo.class);
			if(pi!=null){
				fillSws.put("permission", pi);
			}
			return status;
		}
		return 202;
	}

}
