package cj.lns.chip.sos.website.sws.sns;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.nio.NetConstans;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/session/chatroom.service")
public class ChatroomSessionComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame f = new Frame("open /device/session/chatroom peer/1.0");
		IServicewsContext sws = IServicewsContext.context(frame);
		f.parameter("owner", sws.owner());
		f.parameter("uid", frame.parameter("uid"));
		//cid求出对方的中文名，图片，将对方作为chat应用
		SwsContact contact=getContact(sws.swsid(),frame.parameter("cid"),m);
		String appIcon=contact.getHeadPic();
		if(appIcon==null){
			appIcon="#";
		}
		f.parameter("icon",appIcon);
		f.parameter("memo-name",contact.getMemoName());
		f.parameter("title",String.format("%s",contact.getUserCode()));
		f.parameter("app-code", "chatroom");
		f.parameter("swsid", sws.swsid());
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
				String json = new String(back.content().readFully());
				circuit.content().writeBytes(json.getBytes());
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}
	public SwsContact getContact(String swsid,String cid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getContact /sws/user sos/1.0");
		f.parameter("swsid", swsid);
		f.parameter("cid",cid);
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
				String json = new String(back.content().readFully());
				SwsContact ui = new Gson().fromJson(json,
						SwsContact.class);
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}
}
