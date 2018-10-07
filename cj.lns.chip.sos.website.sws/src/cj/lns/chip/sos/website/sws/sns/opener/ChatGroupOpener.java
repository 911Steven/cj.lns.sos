package cj.lns.chip.sos.website.sws.sns.opener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.sns.IAppSessionOpener;
import cj.lns.chip.sos.website.sws.sns.OpenSessionComponent;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.net.nio.NetConstans;
import cj.studio.ecm.plugins.moduleable.IModuleContext;
import cj.studio.ecm.sns.mailbox.viewer.MessageStub;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "chatGroup")
public class ChatGroupOpener implements IAppSessionOpener {
	@CjServiceInvertInjection
	@CjServiceRef(refByName="/session/openSession.html")
	OpenSessionComponent openSessionComponent;
	public void flow(Map<String, Object> session, Map<String, String> app,
			Frame frame, Circuit circuit) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IModuleContext ctx = m.context();
		IServicewsContext sws = IServicewsContext.context(frame);
		
		List<String> userids = null;
		userids = getChatGroupUsers((String) session.get("appId"), m);
		Document doc = ctx.html("/session/OpenChatGroupSession.html",
				m.site().contextPath(), "utf-8");
		doc.select(".l-c>.left>.filter>.sender").attr("sender",sws.owner());
		doc.select(".l-c>.left>.filter>.sender>ul>li>input[sel]").attr("name",(String)session.get("sid"));
		// 根据用户标识列表查用户信息
		List<SosUser> users = getSosUsers(userids, m);
		Element optionsTitle = doc.select(".l-c>.right>.options>.title").first();
		optionsTitle.html(String.format("联系人(%s)", users.size()));
		Element usersul = doc.select(".l-c>.right>.users").first();
		Element userli = usersul.select(">li").first().clone();
		usersul.empty();
		printSosUsers(users, usersul, userli);
		List< MessageStub> messages = unreadedMessage(sws.owner(),
				sws.swsid(), (String) session.get("sid"), m);// 获取未读的会话消息
		Element msgul = doc.select(".l-c>.left>.cnt").first();
		Element othertmsgli = msgul.select(">li.msg").get(0).clone();
		Element memsgli = msgul.select(">li.msg").get(1).clone();
		msgul.empty();
		Map<String,SosUser> usermap=new HashMap<>();
		for(SosUser u:users){
			usermap.put(u.getUserCode(), u);
		}
		printMsgs(usermap,messages, msgul, othertmsgli, memsgli);
		circuit.content().writeBytes(doc.toString().getBytes());
		flagMessageReaded(sws,(String) session.get("sid"),m);
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

	private void printMsgs(Map<String,SosUser> users, List< MessageStub> messages, Element msgul,
			Element othertmsgli, Element memsgli) {
		for(MessageStub msg:messages){
			othertmsgli=othertmsgli.clone();
			long arriveTime = msg.getArriveTime();
			SimpleDateFormat f=new SimpleDateFormat("MM/dd HH:mm");
			othertmsgli.select(">.time").html(f.format(new Date(arriveTime)));
			
			byte[] body=msg.getBody();
			String text="";
			if(body==null||body.length==0){
				text="&nbsp;";
			}else{
				text=new String(body);
			}
			othertmsgli.select(">.box-other>.tag-left>div").html(text);
			
			SosUser user=users.get(msg.getSender());
			if(user==null)continue;
			String src =String.format("./resource/ud/%s?path=home://system/img/faces&u=%s", user.getHead(),msg.getSender());
			
			othertmsgli.select(">.box-other>.person>img").attr("src",src);
			String name=String.format("%s[%s]", user.getNickName(),user.getUserCode());
			othertmsgli.select(">.box-other>.person>p").html(name);
			
			msgul.appendChild(othertmsgli);
		}
	}

	private void printSosUsers(List<SosUser> users, Element usersul,
			Element userli) {
		for (SosUser u : users) {
			Element li = userli.clone();
			li.attr("uid",u.getUserCode());
			String src =String.format("./resource/ud/%s?path=home://system/img/faces&u=%s", u.getHead(),u.getUserCode());
			
			li.select(">img").attr("src",src);
			li.select(">ul.intro>.f1").html(String.format("%s",u.getUserCode()));
			li.select(">ul.intro>.f2").html(u.getSignatureText()==null?"&nbsp;":u.getSignatureText());
			usersul.appendChild(li);
		}
	}

	private List<MessageStub> unreadedMessage(String uid, String swsid,
			String sid, IServiceosWebsiteModule m) {
		Frame f = new Frame(
				"getUnreadedMessages /device/mailbox/inbox peer/1.0");
		f.parameter("swsid", swsid);
		f.parameter("uid", uid);
		f.parameter("sid", sid);
		f.parameter("sort","1");
		f.parameter("skip","0");
		f.parameter("limit","10000");
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
				List<MessageStub> ui = new Gson().fromJson(json,
						new TypeToken<ArrayList< MessageStub>>() {
						}.getType());
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}

	}

	public Map<String, Object> getSession(String swsid, String uid, String sid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("open /device/session.service peer/1.0");
		f.parameter("swsid", swsid);
		f.parameter("uid", uid);
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
				String json = new String(back.content().readFully());
				Map<String, Object> ui = new Gson().fromJson(json,
						new TypeToken<HashMap<String, Object>>() {
						}.getType());
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	public List<String> getChatGroupUsers(String gid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getUsers /device/session/chatGroup peer/1.0");
		f.parameter("gid", gid);
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
				List<Map<String, String>> ui = new Gson().fromJson(json,
						new TypeToken<ArrayList<HashMap<String, String>>>() {
						}.getType());
				List<String> list = new ArrayList<>();
				for (Map<String, String> user : ui) {
					list.add(user.get("user"));
				}
				return list;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	public List<SosUser> getSosUsers(List<String> userCodes,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getUsersWhereIn /public/user/ sos/1.0");
		f.parameter("userCodes", new Gson().toJson(userCodes));
		Circuit c = new Circuit("sos/1.0 200 OK");
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
				List<SosUser> ui = new Gson().fromJson(json,
						new TypeToken<ArrayList<SosUser>>() {
						}.getType());
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}
}
