package cj.lns.chip.sos.website.security.service;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.service.sws.security.AuthLoggerInfo;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.security.IPaService;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "paService")
public class PaService implements IPaService {

	@Override
	public void allowRole(String resourceId, String valueId, String permCode,
			String permName, String roleCode, String swsId)
					throws CircuitException {
		Frame frame = new Frame("allowRole /sws/security/permission sos/1.0");
		frame.parameter("roleCode", roleCode);
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("permissionName", permName);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}

	}

	@Override
	public void allowContactGroup(String groupId, String resourceId,
			String valueId, String permCode, String permName, String swsId)
					throws CircuitException {
		Frame frame = new Frame("allowGroup /sws/security/permission sos/1.0");
		frame.parameter("groupId", groupId);
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("permissionName", permName);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

	@Override
	public void denyContactGroup(String groupId, String resourceId,
			String valueId, String permCode, String swsId)
					throws CircuitException {
		Frame frame = new Frame("denyGroup /sws/security/permission sos/1.0");
		frame.parameter("groupId", groupId);
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

	@Override
	public void allowContacts(String users, String resourceId, String valueId,
			String permCode, String permName, String swsId)
					throws CircuitException {
		Frame frame = new Frame(
				"allowContacts /sws/security/permission sos/1.0");
		frame.parameter("userCodes", users);
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("permissionName", permName);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

	@Override
	public void denyContacts(String users, String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException {
		Frame frame = new Frame(
				"denyContacts /sws/security/permission sos/1.0");
		frame.parameter("userCodes", users);
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

	@Override
	public List<ContactInfo> getAllowContacts(String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException {
		Frame frame = new Frame(
				"getAllowContacts /sws/security/permission sos/1.0");
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		try {
			List<ContactInfo> users = new Gson().fromJson(json,
					new TypeToken<ArrayList<ContactInfo>>() {
					}.getType());
			return users;
		} catch (TypeNotPresentException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<ContactInfo> getDenyContacts(String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException {
		Frame frame = new Frame(
				"getDenyContacts /sws/security/permission sos/1.0");
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		try {
			List<ContactInfo> users = new Gson().fromJson(json,
					new TypeToken<ArrayList<ContactInfo>>() {
					}.getType());
			return users;
		} catch (TypeNotPresentException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<AuthAskInfo> getQuestionList(String resourceId, String valueId,
			String permCode, String swsid) throws CircuitException {
		Frame frame = new Frame(
				"getAuthAsks /sws/security/auth/answer sos/1.0");
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsid);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		try {
			List<AuthAskInfo> users = new Gson().fromJson(json,
					new TypeToken<ArrayList<AuthAskInfo>>() {
					}.getType());
			return users;
		} catch (TypeNotPresentException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<AuthLoggerInfo> getAuthLogger(String resourceId, String valueId,
			String permCode, String swsid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthAskInfo addQuestion(String a, String q, String resourceId,
			String valueId, String permCode, String permName, String swsId) throws CircuitException {
		Frame frame = new Frame(
				"createQuestion /sws/security/auth/answer sos/1.0");
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		frame.parameter("a",a);
		frame.parameter("q",q);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		if(back.content().readableBytes()>0){
			AuthAskInfo ai=new Gson().fromJson(new String(back.content().readFully()), AuthAskInfo.class);
			return ai;
		}
		return null;
	}

	@Override
	public void delQuestion(String aqid, String a,String q,String resourceId, String valueId,
			String permCode, String swsId) throws CircuitException {
		Frame frame = new Frame(
				"delQuestion /sws/security/auth/answer sos/1.0");
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		frame.parameter("aqid",aqid);
		frame.parameter("a",a);
		frame.parameter("q",q);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

	@Override
	public void updateQuestion(String aqid, String a, String q,
			String resourceId, String valueId, String permCode, String swsId) throws CircuitException {
		Frame frame = new Frame(
				"updateQuestion /sws/security/auth/answer sos/1.0");
		frame.parameter("objectName", resourceId);
		frame.parameter("objectInst", valueId);
		frame.parameter("permissionCode", permCode);
		frame.parameter("swsId", swsId);
		frame.parameter("aqid",aqid);
		frame.parameter("a",a);
		frame.parameter("q",q);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(frame, circuit);
		Frame back = new Frame(circuit.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

	
}
