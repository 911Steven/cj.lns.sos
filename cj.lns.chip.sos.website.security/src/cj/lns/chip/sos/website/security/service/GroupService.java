package cj.lns.chip.sos.website.security.service;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.service.sws.user.ContactInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.security.IContactGroupService;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "groupService")
public class GroupService implements IContactGroupService {
	@Override
	public List<ContactGroupInfo> getContactGroups(String swsid) throws CircuitException {
		Frame frame = new Frame("getContactGroups /sws/userGroup/ sos/1.0");
		frame.parameter("swsid", swsid);
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
			List<ContactGroupInfo> roles = new Gson().fromJson(json,
					new TypeToken<ArrayList<ContactGroupInfo>>() {
					}.getType());
			return roles;
		} catch (TypeNotPresentException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<ContactInfo> getContactsByGroup(String gid,String swsid)
			throws CircuitException {
		Frame frame = new Frame("getContactsByGroup /sws/userGroup/ sos/1.0");
		frame.parameter("swsid", swsid);
		frame.parameter("groupId",gid);
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

}
