package cj.lns.chip.sos.website.market.app.contact.component;

import java.math.BigInteger;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.user.ContactGroupInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "/viewUser.html")
public class ViewUserComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String uid = frame.parameter("uid");

		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();

		Frame f = new Frame("getUser /public/user/ sos/1.0");
		f.parameter("uid", uid);
		Circuit c = new Circuit("sos/1.0 200 ok");
		m.site().out().flow(f, c);
		Frame back = new Frame(c.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		SosUserInfo user = new Gson().fromJson(json, SosUserInfo.class);
		Document doc = m.context().html("/contact/viewUser.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext sws=IServicewsContext.context(frame);
		render(doc, user,m,sws);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void render(Document doc, SosUserInfo user, IServiceosWebsiteModule m, IServicewsContext sws) throws CircuitException {
		doc.select(".u-view").attr("uid", user.getId().toString());
		Element e = doc.select(".u-pic>ul>li.u-profile").first();
		if (!StringUtil.isEmpty(user.getHead()))
			e.select("img").attr("src", user.getHead());
		String name = user.getUserCode();
		if (StringUtil.isEmpty(name)) {
			name = user.getUserCode();
		}
		e.select("div[nickname]").html(name);
		if (StringUtil.isEmpty(user.getSignatureText())) {
			e.select("div[text]").html("&nbsp;");
		} else {
			e.select("div[text]").html(user.getSignatureText());
		}
		Element op = doc.select(".u-pic>ul>li.u-op").first();
		//1.是否已加入到了视窗且加入的组是否为公众分组，从而打印：已关注到公众，或已为好友，if='off'表示未加入，ON为已加入
		//2.获取分组并打印
		//3.如果已是公众，可移组到好友分组
		//4.如果 是自已则编辑，这搞得功能太多了，先不实现。
		ContactGroupInfo g=getUserGroup(m,sws.swsid(),user.getId());
		Element select=op.select("a[a=friend]>ul").first();
		if(g!=null){
			Element pub=op.select("a[a=public]").first();
			if("open".equals(g.getGroupType())){//已是公众,还可加为好友，因此要取出视窗的分组列表
				
				pub.html("已关注到公众分组");
				pub.attr("if","on");
				printGroupList(m,sws.swsid(),select);
			}else{//已是好友，则隐藏公众，无需取分组列表了。
				Element fri=op.select("a[a=friend]").first();
				fri.html(String.format("已在分组%s",g.getGroupName()));
				fri.attr("if","on");
				pub.attr("if","on");//表示无需再添加到公众了。
			}
			return;
		}
		//即不是公众也不是好友，就都ON吧
		printGroupList(m,sws.swsid(),select);
	}

	private void printGroupList(IServiceosWebsiteModule m, String swsid, Element select) throws CircuitException {
		Frame f = new Frame("getContactGroups /sws/userGroup/ sos/1.0");
		f.parameter("swsid", swsid);
		Circuit c = new Circuit("sos/1.0 200 ok");
		m.site().out().flow(f, c);
		Frame back = new Frame(c.content().readFully());
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		List<ContactGroupInfo> groups=new Gson().fromJson(json, new TypeToken<List<ContactGroupInfo>>(){}.getType());
		Element li=select.select("li").first().clone();
		select.empty();
		for(ContactGroupInfo g:groups){
			li=li.clone();
			li.attr("gid",g.getId().toString());
			li.html(g.getGroupName());
			select.appendChild(li);
		}
	}

	private ContactGroupInfo getUserGroup(IServiceosWebsiteModule m,
			String swsid, BigInteger uid) throws CircuitException {
		Frame f = new Frame("getGroupByContact /sws/userGroup/ sos/1.0");
		f.parameter("swsid", swsid);
		f.parameter("uid",uid.toString());
		Circuit c = new Circuit("sos/1.0 200 ok");
		m.site().out().flow(f, c);
		Frame back = new Frame(c.content().readFully());
		if("404".equals(back.head("status"))){
			return null;
		}
		if (!"200".equals(back.head("status"))) {
			throw new CircuitException(back.head("status"),
					String.format("在远程服务器上出错，原因：%s", back.head("message")));
		}
		String json = new String(back.content().readFully());
		return new Gson().fromJson(json, ContactGroupInfo.class);
	}

}
