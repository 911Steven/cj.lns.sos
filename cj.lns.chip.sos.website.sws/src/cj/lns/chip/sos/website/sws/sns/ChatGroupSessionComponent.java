package cj.lns.chip.sos.website.sws.sns;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.nio.NetConstans;

@CjService(name = "/session/chatGroup.service")
public class ChatGroupSessionComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
//		String action = "yes";
//		if (!"yes".equals(frame.parameter("action"))) {
//			action = "no";
//		}
//		if ("yes".equals(action)) {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			Frame f = new Frame("open /device/session/chatGroup peer/1.0");
			IServicewsContext sws = IServicewsContext.context(frame);
			f.parameter("owner", sws.owner());
			f.parameter("app-code", "chatGroup");
			f.parameter("app-id", frame.parameter("gid"));
			f.parameter("swsid", sws.swsid());
			Circuit c = new Circuit("peer/1.0 200 OK");
			f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
			// 等待15秒
			try {
				m.site().out().flow(f, c);
				if ("frame/bin".equals(c.contentType())) {
					Frame back = new Frame(c.content().readFully());
					if (!"200".equals(back.head("status"))) {
						throw new CircuitException(back.head("status"),
								String.format("在远程服务器上出现错误。原因：%s",
										back.head("message")));
					}
					String json = new String(back.content().readFully());
					circuit.content().writeBytes(json.getBytes());
				}
			} catch (CircuitException e1) {
				throw new EcmException(e1);
			}
//		}else{
//			//向发送者告知被拒
//		}
	}

}
