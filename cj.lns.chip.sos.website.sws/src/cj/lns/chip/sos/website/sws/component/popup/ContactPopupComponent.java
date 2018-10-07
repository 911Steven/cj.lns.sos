package cj.lns.chip.sos.website.sws.component.popup;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.chip.sos.website.sws.so.ContactSO;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

/*
 * 在进入视窗之前验证：
 * · 有没有登录服务操作系统
 * · 视窗是不是开放视窗，即开放级别
 *   是开放给sos，还是开放给大众。如果是前者，则来访者必须先登录sos，否则不能使用。如果是开放给大众，不论是否登录sos都可访问视窗
 * · 是不是视窗的持有人来访
 *   如果是则全权访问
 * · 是联系人来访，调出联系人认证界面
 *   联系人角色、归属组等，采用基于角色的权限认证。如果联系人属于管理员角色，则和持有人一样拥有全权。
 *   
 *   -> sos   ->   sws
 *     统一认证    视窗认证
 *     语义为：谁来访问视窗，在同一会话中切换多视窗(任何视窗），在时间切片上最多只存在一个视窗
 */
@CjService(name = "/components/ContactPopup.html")
public class ContactPopupComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/components/contact-popup.html",
				m.site().contextPath(), "utf-8");
		String finder = "/security/resourceChildsFinder.service";
		if (frame.containsQueryString()) {
			finder = String.format("%s?%s", finder, frame.queryString());
		}
		frame.url(finder);
		frame.parameter("rootResourceId", "contact");
		frame.parameter("rootValueId", "servicews.contact");
		frame.parameter("resourceId", "contact");
		frame.parameter("valueId", "servicews.contact");
		m.site().out().flow(frame, circuit);
		String json = new String(circuit.content().readFully());
		List<ContactSO> list = new Gson().fromJson(json,
				new TypeToken<List<ContactSO>>() {
				}.getType());
		renderCategory(doc, list);
		renderOwnerInfo(doc, frame);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void renderOwnerInfo(Document doc, Frame frame) {
		IServicewsContext ctx = IServicewsContext.context(frame);
		SosUserInfo user = sws.getOwner(ctx.swsid());
		Element card = doc.select(".contact-panel>.ct-my>.card").first();
		if (!StringUtil.isEmpty(user.getHead())) {
			String src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s", ctx.face().getHead(),ctx.owner());
			card.select(".card-head>img").attr("src", src);
		}
		Element text=card.select(".card-text").first();
		text.select("li[name]").html(user.getUserCode());
		if (user.getSignatureText() == null){
			text.select("li[text]").html("&nbsp;");
		}else{
			text.select("li[text]").html(user.getSignatureText());
		}
	}

	private void renderCategory(Document doc, List<ContactSO> list) {
		Element ul = doc.select(".contact-panel div[category]>ul").first();
		Element li = ul.select("li").last().clone();
		ul.empty();
		Element tmp = li.clone();
		for (ContactSO so : list) {
			if (!StringUtil.isEmpty(so.getId()))
				tmp.attr("tab", so.getId());
			tmp.html(so.getName());
			if ("contact.group".equals(so.getId())) {
				tmp.addClass("selected");
			}
			if (!StringUtil.isEmpty(so.getCommand())) {
				tmp.attr("src", so.getCommand());
			}
			ul.appendChild(tmp);
			tmp = li.clone();
		}
	}

}
