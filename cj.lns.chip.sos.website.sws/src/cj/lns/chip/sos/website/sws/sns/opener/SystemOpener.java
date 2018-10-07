package cj.lns.chip.sos.website.sws.sns.opener;

import java.util.Map;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.sns.IAppSessionOpener;
import cj.lns.chip.sos.website.sws.sns.OpenSessionComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.net.nio.NetConstans;
import cj.studio.ecm.sns.mailbox.Mailbox;

@CjService(name = "system")
public class SystemOpener implements IAppSessionOpener {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "/session/openSession.html")
	OpenSessionComponent openSessionComponent;

	@Override
	public void flow(Map<String, Object> session, Map<String, String> app,
			Frame frame, Circuit circuit) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);
		renderTaskMessagePad(sws, m,frame,circuit);
		flagMessageReaded(sws, (String) session.get("sid"), m);
	}

	private void renderTaskMessagePad(IServicewsContext sws,
			IServiceosWebsiteModule m,  Frame frame, Circuit circuit) throws CircuitException {
		frame.url("/cyberportApp/popup/getMessages.html");
		frame.parameter("action", Mailbox.inbox.name());
		m.site().out().flow(frame, circuit);
//		byte[] b=circuit.content().readFully();
//		String html=new String(b);
//		StringBuffer sb=new StringBuffer();
//		sb.append("<div class='sys-box' style='height:100%;overflow:auto;'>");
//		sb.append(html);
//		sb.append("</div>");
//		circuit.content().writeBytes(sb.toString().getBytes());
	}

	private void flagMessageReaded(IServicewsContext sws, String sid,
			IServiceosWebsiteModule m) throws CircuitException {
		Frame f = new Frame("flagMessageReaded /device/mailbox/inbox peer/1.0");
		f.parameter("owner", sws.owner());
		f.parameter("swsid", sws.swsid());
		f.parameter("sid", sid);
		Circuit c = new Circuit("peer/1.0 200 OK");
		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}

			}
		} catch (CircuitException e1) {
			throw e1;
		}
	}

}
