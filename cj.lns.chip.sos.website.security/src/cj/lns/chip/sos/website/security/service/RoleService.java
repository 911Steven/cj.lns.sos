package cj.lns.chip.sos.website.security.service;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.RoleInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.security.IRoleService;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "roleService")
public class RoleService implements IRoleService {

	@Override
	public List<RoleInfo> getRoles(String swsid) throws CircuitException {
		Frame frame = new Frame("getRoles /sws/security/role/ sos/1.0");
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
			List<RoleInfo> roles = new Gson().fromJson(json,
					new TypeToken<ArrayList<RoleInfo>>() {
					}.getType());
			return roles;
		} catch (TypeNotPresentException e) {
			return new ArrayList<>();
		}
	}


}
