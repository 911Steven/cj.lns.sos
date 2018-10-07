package cj.lns.chip.sos.website.market.app.contact.component;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/findUsers.service")
public class FindUsersService implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit,IPlug plug) throws CircuitException {
		String online=frame.parameter("online");
		String sex=frame.parameter("sex");
		String userCode=frame.parameter("userCode");
		
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		
		Frame f = new Frame("findUserList /public/user/ sos/1.0");
		f.parameter("online", online);
		f.parameter("sex", sex);
		f.parameter("userCode",userCode);
		f.parameter("start",frame.parameter("start"));
		f.parameter("max",frame.parameter("max"));
		Circuit c = new Circuit("sos/1.0 200 ok");
		m.site().out().flow(f, c);
		Frame back = new Frame(c.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
//		String json=new String(back.content().readFully());
//		List<SosUserInfo> users=new Gson().fromJson(json, new TypeToken<List<SosUserInfo>>(){}.getType());
//		System.out.println(users);
		circuit.content().writeBytes(back.content().readFully());
	}

}
