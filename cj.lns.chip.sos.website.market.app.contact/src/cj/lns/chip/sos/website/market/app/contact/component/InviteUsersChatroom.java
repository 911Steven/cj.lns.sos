package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IDocument;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.sns.mailbox.viewer.MessageStub;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

@CjService(name = "/inviteUserChatroom.service")
public class InviteUsersChatroom implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);

		String users = frame.parameter("users");
		String sid = frame.parameter("sid");

		INetDisk userdisk = m.site().diskOwner(sws.owner());
		ICube userhome = userdisk.cube(sws.swsid());
		String cjql = "select {'tuple':'*'} from tuple sns.session java.util.HashMap where {'_id':ObjectId('?(sid)')}";
		IQuery<Map<String, Object>> q = userhome.createQuery(cjql);
		q.setParameter("sid", sid);
		IDocument<Map<String, Object>> s = q.getSingleResult();
		if (s == null) {
			throw new CircuitException("404",
					String.format("用户:%s没有该会话:%s", sws.owner(), sid));
		}
		String appId=(String) s.tuple().get("appId");
		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		cjql = "select {'tuple':'*'} from tuple sns.chatroom.users java.util.HashMap where {'tuple.crid':'?(crid)','tuple.user':{$in:?(user)}}";
		IQuery<Map<String, Object>> q2 = home.createQuery(cjql);
		q2.setParameter("crid", appId);
		String[] uarr = users.split(",");
		List<String> uids = new ArrayList<>();
		for (String uid : uarr) {
			if (StringUtil.isEmpty(uid)) {
				continue;
			}
			uids.add(String.format("'%s'", uid));
		}
		q2.setParameter("user", uids);
		List<IDocument<Map<String, Object>>> list = q2.getResultList();
		if (list.size() > 0) {// 我已加入则返回
			String exists = "这些用户已在你的会话室中:";
			for (IDocument<Map<String, Object>> doc : list) {
				exists += String.format("%s ", doc.tuple().get("user"));
			}
			throw new CircuitException("503", exists);
		}
		MessageStub msg = new MessageStub();
		msg.setSender(sws.owner());
		msg.setSenderOnSws(sws.swsid());
		msg.setSendTime(System.currentTimeMillis());
		msg.setSid(sid);
		msg.setSwstid(sws.swstid());
		StringBuffer sb=new StringBuffer();
		sb.append("<script>");
		sb.append("(function(){");
		sb.append("$('div[embeds]>span[crid]>a[action]').on('click',function(){");
		sb.append("var action=$(this).attr('action');");
		sb.append("var the=$(this);");
		sb.append("var crid=the.parent('span[crid]').attr('crid');");
		sb.append("$.get('./contact/joinChatroom2.service?action='+action+'&crid='+crid,{},function(data){");
		sb.append("the.css('color','gray');");
		sb.append("});");
		sb.append("});");
		sb.append("})();");
		sb.append("</script>");
		sb.append(String.format("<div embeds>%s 邀请你加入会话室。<span crid='%s'><a href='#' action='yes' style='font-size:14px;color:blue;' >同意</a>&nbsp;&nbsp;<a href='#' action='no' style='font-size:14px;color:blue;' >拒绝</a></span></div>", sws.owner(),appId));
		msg.setBody(sb.toString().getBytes());
		inviteSend(msg, uarr, appId, m);
	}

	private void inviteSend(MessageStub msg, String[] userCodes, String appId,
			IServiceosWebsiteModule m) throws CircuitException {
		Frame f = new Frame("pushMsg /im/session.service im/1.0");
		f.parameter("app-id", String.format("chatroom.%s", appId));
		f.parameter("app-code", "system");//系统通知
		f.parameter("users",new Gson().toJson(userCodes));
		f.content().writeBytes(new Gson().toJson(msg).getBytes());
		Circuit c = new Circuit("im/1.0 200 ok");
		m.site().out().flow(f, c);
	}
}
