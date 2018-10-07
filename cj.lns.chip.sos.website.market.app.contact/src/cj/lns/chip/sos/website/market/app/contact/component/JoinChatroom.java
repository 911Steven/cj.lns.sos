package cj.lns.chip.sos.website.market.app.contact.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.cube.framework.IDocument;
import cj.lns.chip.sos.cube.framework.IQuery;
import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.market.app.contact.bo.ChatGroupBO;
import cj.lns.common.sos.service.model.SosUser;
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
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "/joinChatroom.service")
public class JoinChatroom implements IComponent {

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

		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		cjql = "select {'tuple':'*'} from tuple sns.chatroom.users java.util.HashMap where {'tuple.crid':'?(crid)','tuple.user':{$in:?(user)}}";
		IQuery<Map<String, Object>> q2 = home.createQuery(cjql);
		q2.setParameter("crid", s.tuple().get("appId"));
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
		for(String uid:uarr){
		Map<String, Object> map = new HashMap<>();
		map.put("crid", s.tuple().get("appId"));
		map.put("user", uid);
		TupleDocument<Map<String, Object>> newdoc = new TupleDocument<Map<String, Object>>(
				map);
		home.saveDoc("sns.chatroom.users", newdoc);
		}
		
		List<SosUser> sosusers = getSosUsers(uarr, m);
		// printSosUsers(sosusers, usersul, userli);

	}

	public List<SosUser> getSosUsers(String[] userCodes,
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

	private void printSosUsers(List<SosUser> users, Element usersul,
			Element userli) {
		for (SosUser u : users) {
			Element li = userli.clone();
			li.attr("uid", u.getUserCode());
			String src = u.getHead();
			if (StringUtil.isEmpty(src)) {
				src = "#";
			}
			li.select(">img").attr("src", src);
			li.select(">ul.intro>.f1").html(
					String.format("%s[%s]", u.getNickName(), u.getUserCode()));
			li.select(">ul.intro>.f2").html(u.getSignatureText() == null
					? "&nbsp;" : u.getSignatureText());
			usersul.appendChild(li);
		}
	}

}
