package cj.lns.chip.sos.website.market.app.contact.component;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/delGroup.service")
public class DelGroupService implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String gid = frame.parameter("gid");
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);
		Frame f = new Frame("deleteContactGroup /sws/userGroup/ sos/1.0");
		f.parameter("swsid", sws.swsid());
		f.parameter("gid", gid);
		Circuit c = new Circuit("sos/1.0 200 ok");
		m.site().out().flow(f, c);
		Frame back = new Frame(c.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
	}

}
