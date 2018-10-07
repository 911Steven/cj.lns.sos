package cj.lns.chip.sos.website.market.app.cyberport.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.service.sws.ServicewsSummary;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.net.nio.NetConstans;
import cj.studio.ecm.sns.mailbox.Message;
import cj.studio.ecm.sns.mailbox.viewer.InboxSessionViewer;
import cj.studio.ecm.sns.mailbox.viewer.MailboxMessageViewer;
import cj.studio.ecm.sns.mailbox.viewer.MessageStub;
import cj.studio.ecm.sns.mailbox.viewer.MySessionStub;
import cj.studio.ecm.sns.mailbox.viewer.SnsAppStub;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "FetchServciewsInfo")
public class FetchServciewsInfo {
	public void printViewer(Element appUl, InboxSessionViewer viewer) {
		List<SnsAppStub> apps = viewer.getApps();
		Element appLi = appUl.select("#cyb-app").first().clone();
		appUl.empty();
		for (SnsAppStub app : apps) {
			List<MySessionStub> sessions = app.getSessions();
			if (sessions.isEmpty()) {
				continue;
			}
			appLi = appLi.clone();
			
			appLi.select(">.head>img").attr("src", app.getIcon());
			
			String appcode=app.getCode() == null ? "" : app.getCode();
			appLi.attr("appCode",appcode);
			Element codeli = appLi.select(">.head>.detail>li[appCode]").first();
			codeli.attr("appCode",appcode);
			codeli.html(app.getName());
			long count = app.totalMessages();
			if (count > 0) {
				appLi.select(">.head>.tips>.cal>span").first()
						.html(String.valueOf(count));
				appLi.select(">.head>.tips>.time").first().html(app.getTime());
				Element textli = appLi.select(">.head>.detail>li[appText]")
						.first();
				textli.html(String.format(
						"<span>%s</span>&nbsp;&nbsp;<span>%s</span>",
						app.getSender(), app.getText()));
			} else {
				appLi.select(">.head>.tips>.cal>span").first().attr("style",
						"display:none;");

				appLi.select(">.head>.tips>.time").first().attr("style",
						"display:none;");
				Element textli = appLi.select(">.head>.detail>li[appText]")
						.first();
				textli.html(String.format(
						"<span>%s</span>&nbsp;&nbsp;<span>%s</span>",
						app.getSender(), app.getText()));
			}
			Element boxul = appLi.select(">.box").first();
			Element litem = boxul.select(">li.item").first().clone();
			boxul.empty();

			for (MySessionStub session : sessions) {
				litem = litem.clone();
				litem.attr("sid", session.getSid());
				litem.select(">img").attr("src",
						session.getIcon() == null ? "#" : session.getIcon());
				litem.select(">.detail>li[ltitle]").html(session.getTitle());
				Message msg = session.getNewestMsg();
				if (msg != null) {
					String text = "";
					if(session.getSnsApp()!=null&&"chatroom".equals(session.getSnsApp().getCode())){
						 text = String.format(
									"<span>%s</span>", new String(msg.getBody()));
					}else{
						 text = String.format(
									"<span>%s</span>:&nbsp;&nbsp;<span>%s</span>",
									msg.getSender(), new String(msg.getBody()));
					}
					litem.select(">.detail>li[ltext]").html(text);

					litem.select(">.tips>.cal>span")
							.html(String.valueOf(session.getTotalMsgs()));
					long mtime = msg.getArriveTime();
					if (mtime != -1) {
						Date time = new Date(mtime);
						String dtime = new SimpleDateFormat(
								"hh:mm:ss").format(time);
						String day = new SimpleDateFormat(
								"yyyy-MM-dd").format(time);
						Element timer=litem.select(">.tips>.time").first();
						timer.html(dtime);
						timer.attr("title",day);
					}
				} else {
					litem.select(">.tips>.cal>span").remove();
					litem.select(">.detail>li[ltext]").html("&nbsp;");
					litem.select(">.tips>.time").first().html("&nbsp;");
				}
				boxul.appendChild(litem);
			}
			appUl.appendChild(appLi);
		}
	}

	public ServicewsSummary getServicewsSummary(String swsid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getServicewsSummary /sws/instance sos/1.0");
		f.parameter("swsid", swsid);
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
				ServicewsSummary ui = new Gson().fromJson(json,
						ServicewsSummary.class);
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	public SosUserInfo getUserServicewsSummaries(String user,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getAllSws /sws/owner sos/1.0");
		f.parameter("userCode", user);
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
				SosUserInfo si = new Gson().fromJson(json,
						SosUserInfo.class);
				return si;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	public InboxSessionViewer getUserSessions(String owner, String swsid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getCyberport /device/mailbox/inbox peer/1.0");
		f.parameter("user", owner);
		f.parameter("swsid", swsid);
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
				InboxSessionViewer viewer = new Gson().fromJson(json,
						InboxSessionViewer.class);
				return viewer;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}

	}

	public Map<String, Long> getMessageTotal(String owner, String swsid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getMessageTotal /device/mailbox/inbox peer/1.0");
		f.parameter("user", owner);
		f.parameter("swsid", swsid);
		Circuit c = new Circuit("peer/1.0 200 OK");
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
				Map<String, Long> map = new Gson().fromJson(json,
						new TypeToken<HashMap<String, Long>>() {
						}.getType());
				return map;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	public void printMessageTotal(Element tips, Map<String, Long> total) {
		/*
		 *<ul class="tips" title="按消息摘要列表方式显示">
						<li inbox><span>入港</span>
							<p>10</p></li>
						<li outbox><span>出港</span>
							<p>10</p></li>
						<li drafts><span>任务</span>
							<p>10</p></li>
						<li trash><span>网关</span>
							<p>100</p></li>
					</ul>
		 */
		tips.select(">li[inbox]>p").first()
				.html(String.valueOf(total.get("inbox")));
		tips.select(">li[outbox]>p").first()
				.html(String.valueOf(total.get("outbox")));
		tips.select(">li[drafts]>p").first()
				.html(String.valueOf(total.get("drafts")));
		tips.select(">li[trash]>p").first()
				.html(String.valueOf(total.get("trash")));
	}

	public MailboxMessageViewer getAllMessage(String owner, String swsid,
			String action, String skip, String limit,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getAllMessage /device/mailbox/inbox peer/1.0");
		f.parameter("user", owner);
		f.parameter("swsid", swsid);
		f.parameter("mailbox", action);
		f.parameter("skip", skip);
		f.parameter("limit", limit);
		Circuit c = new Circuit("peer/1.0 200 OK");
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
				MailboxMessageViewer msgs = new Gson().fromJson(json,
						MailboxMessageViewer.class);
				return msgs;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}

	}

	public SosUserInfo getUser(String owner, IServiceosWebsiteModule m) {
		Frame f = new Frame("findUserList /public/user/ sos/1.0");
		f.parameter("userCode", owner);
		f.parameter("start", "0");
		f.parameter("max", "1");
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
				List<SosUserInfo> list = new Gson().fromJson(json,
						new TypeToken<ArrayList<SosUserInfo>>() {
						}.getType());
				if (list.isEmpty())
					return null;
				return list.get(0);
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}

	}

	public void printMessages(String action, MailboxMessageViewer view,
			Element li, Element c, IServiceosWebsiteModule mod) {
		/*
		 * <ul class="msg-box">
		<li class="head"><img src="img/5.jpg">
			<ul class="sign">
				<li class="up">carocean</li>
				<li class="down">生当做人杰</li>
			</ul>
			<ul class="m-right">
				<li class="up">9:45</li>
				<li class="down">应用 聊天</li>
			</ul></li>
		<li class="body">央视快讯：中国国民党今天举行党主席补选投票。在四位候选人中，前立法机构副主管洪秀柱在大部分县市得票都遥遥领先，最终以78000多票，56.16%的得票率当选国民党新任党主席，任期至2017年8月，递补马英九、朱立伦未完成的任期</li>
		<li class="bottom">&nbsp;<div><span>来自</span> <a href="#">李梦阳 的会话室</a></div></li>
		</ul>
		 */
		Map<String, MySessionStub> sessions = view.getSessions();
		Iterator<Entry<String, MessageStub>> it = view.getMessages().entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, MessageStub> en = it.next();
			MessageStub m = en.getValue();
			MySessionStub session = sessions.get(m.getSid());
			SosUserInfo user = getUser(m.getSender(), mod);
			li = li.clone();
			if (user != null) {
				li.attr("sid", m.getSid());
				String src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s", user.getHead(),user.getUserCode());
				li.select(">ul.msg-box>.head>img").attr("src",src);
				li.select(">ul.msg-box>.head>.sign>.up").html(String.format(
						"%s",user.getUserCode()));
				String sign = user.getSignatureText();
				if (sign == null) {
					sign = "&nbsp;";
				}
				li.select(">ul.msg-box>.head>.sign>.down").html(sign);
			}
			long ltime = -1;
			if ("outbox".equals(action)) {
				ltime = m.getSendTime();
			} else {
				ltime = m.getArriveTime();
			}
			if (ltime != -1) {
				Date time = new Date(ltime);
				String day = new SimpleDateFormat(
						"yyyy-MM-dd").format(time);
				String dtime = new SimpleDateFormat("hh:mm:ss")
						.format(time);
				Element timer=li.select(">ul.msg-box>.head>.m-right>.up").first();
				timer.html(dtime);
				timer.attr("title",day);
			} else {
				li.select(">ul.msg-box>.head>.m-right>.up").html("");
			}
			Element app = li.select(">ul.msg-box>.head>.m-right>.down").first();
			if (sessions.containsKey(m.getSid())) {
				app.attr(session.getSnsApp().getCode());
				app.html(session.getSnsApp().getName());
				Element la = li.select(">ul.msg-box>.bottom>div>a").first();
				la.html(session.getTitle());
				la.attr("sid", session.getSid());
			}else{
				app.attr("&nbsp;");
				app.html("&nbsp;");
				Element la = li.select(">ul.msg-box>.bottom>div>a").first();
				la.html("&nbsp;");
				Element span = li.select(">ul.msg-box>.bottom>div>span").first();
				span.html("&nbsp;");
			}
			li.select(">ul.msg-box>.body").html(new String(m.getBody()));
			c.appendChild(li);
		}
	}
}
