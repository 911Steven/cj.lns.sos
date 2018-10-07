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
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "dynamic")
public class DynamicOpener implements IAppSessionOpener {
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "/session/openSession.html")
	OpenSessionComponent openSessionComponent;

	public void flow(Map<String, Object> session, Map<String, String> app,
			Frame frame, Circuit circuit) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);
		flagMessageReaded(sws,(String) session.get("sid"),m);
		circuit.content().writeBytes(new Gson().toJson(session).getBytes());
	}
	private void flagMessageReaded(IServicewsContext sws, String sid, IServiceosWebsiteModule m) throws CircuitException {
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
