package cj.lns.chip.sos.website.auth.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.website.auth.IAuthFactory;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

/**
 * 认证组件
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "/listServicews.html")
public class ListServicews implements IComponent {
	@CjServiceRef(refByName = "authFactory")
	IAuthFactory factory;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		ISubject subject = ISubject.subject(frame);
		if (subject == null) {
			throw new CircuitException("404", "没有登录");
		}
		String owner = frame.parameter("owner");
		if (!owner.equals(subject.principal())) {
			throw new CircuitException("503", String.format("当事人：%s 不是持有者：%s",
					subject.principal(), owner));
		}

		SosUserInfo user = getSwsList(subject.principal());
		if (user == null) {
			throw new CircuitException("503", String.format("用户不存在：%s", owner));
		}
		Document doc = m.context().html("/listServicews.html",
				m.site().contextPath(), "utf-8");
		printSwsList(subject, user, doc);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void printSwsList(ISubject subject, SosUserInfo owner,
			Document doc) {
		List<ServicewsInfo> list = null;
		list = owner.getSwsList();
		doc.select(".sws-win > .center > .kind").attr("style", "display:none");
		doc.select(".sws-win > .left > .tips > li[count]>span.value")
				.html(String.valueOf(list.size()));
		doc.select(".sws-win > .left > .tips > li[who]>span.value")
				.html(subject.principal());
		if (owner.getCapacity() == -1) {
			doc.select(".sws-win > .left > .tips > li[nd]>span.value")
					.html("不限");
			doc.select(".sws-win > .left > .tips > li[nd]>span.unit")
					.html("&nbsp;");
			doc.select(".sws-win > .left > .tips > li[remain]>span.value")
					.html("无限");
			doc.select(".sws-win > .left > .tips > li[remain]>span.unit")
					.html("&nbsp;");
		} else {
			double d = owner.getCapacity() / 1024 / 1024 / 1024D;
			doc.select(".sws-win > .left > .tips > li[nd]>span.value")
					.html(String.valueOf(d));

			d = (owner.getCapacity() - owner.getDataSize()) / 1024 / 1024
					/ 1024;
			doc.select(".sws-win > .left > .tips > li[remain]>span.value")
					.html(String.format("%f", d));
		}
		doc.select(".sws-win > .left > .tips > li[cubeCount]>span.value")
		.html(String.format("%s", owner.getCubeCount()));
		
		double d = owner.getUseSpace() / 1024 / 1024 / 1024;
		doc.select(".sws-win > .left > .tips > li[useSpace]>span.value")
				.html(String.format("%f", d));
		d = owner.getDataSize() / 1024 / 1024 / 1024;
		doc.select(".sws-win > .left > .tips > li[dataSpace]>span.value")
				.html(String.format("%f", d));
		if (owner.getHomeCapacity() == -1) {
			doc.select(".sws-win > .left > .tips > li[home]>span.value")
					.html("不限");
			doc.select(".sws-win > .left > .tips > li[home]>span.unit")
					.html("&nbsp;");
		} else {
			d = owner.getHomeCapacity() / 1024 / 1024 / 1024;
			doc.select(".sws-win > .left > .tips > li[home]>span.value")
					.html(String.format("%f", d));
		}

		String role = "个人视窗用户";
		if (subject.containsRole("orgUsers")) {
			role = "公共视窗用户";
		}
		if (subject.containsRole("basicswsUsers")) {
			role = "基础视窗用户";
		}
		if (subject.containsRole("sosUsers")) {
			role = "超级视窗用户";
		}
		doc.select(".sws-win > .left > .tips > li[role]>span.value").html(role);
		Map<Byte, List<ServicewsInfo>> map = new HashMap<>();
		for (ServicewsInfo si : list) {
			List<ServicewsInfo> kind = map.get(si.getLevel());
			if (kind == null) {
				kind = new ArrayList<>();
				map.put(si.getLevel(), kind);
			}
			kind.add(si);
		}

		// 直接打印公共视窗和个人视窗
		// 个人视窗的申请权限对所有人是开放的
		List<ServicewsInfo> kind = map.get((byte) 3);
		Element psws = doc.select(".sws-win > .center > .kind[psws]").first();
		psws.attr("style", "display:block");
		Element ul = psws.select(">ul").first();
		Element li = ul.select(">li").first().clone();
		ul.empty();
		if (kind != null) {
			printServicews(kind, ul, li);
		}
		if (subject.containsRole("orgUsers")
				|| subject.containsRole("basicswsUsers")
				|| subject.containsRole("sosUsers")) {
			// 打印公共视窗
			List<ServicewsInfo> ckind = map.get((byte) 2);
			Element csws = doc.select(".sws-win > .center > .kind[csws]")
					.first();
			csws.attr("style", "display:block");
			Element cul = csws.select(">ul").first();
			Element cli = cul.select(">li").first().clone();
			cul.empty();
			if (ckind != null) {
				printServicews(ckind, cul, cli);
			}
		}
		if (subject.containsRole("basicswsUsers")
				|| subject.containsRole("sosUsers")) {
			// 打印basic视窗
			List<ServicewsInfo> ckind = map.get((byte) 1);
			Element csws = doc.select(".sws-win > .center > .kind[bsws]")
					.first();
			csws.attr("style", "display:block");
			Element cul = csws.select(">ul").first();
			Element cli = cul.select(">li").first().clone();
			cul.empty();
			if (ckind != null) {
				printServicews(ckind, cul, cli);
			}
		}
		if (subject.containsRole("sosUsers")) {
			// 打印超级视窗
			List<ServicewsInfo> ckind = map.get((byte) 0);
			Element csws = doc.select(".sws-win > .center > .kind[ssws]")
					.first();
			csws.attr("style", "display:block");
			Element cul = csws.select(">ul").first();
			Element cli = cul.select(">li").first().clone();
			cul.empty();
			if (ckind != null) {
				printServicews(ckind, cul, cli);
			}
		}
	}

	private void printServicews(List<ServicewsInfo> kind, Element ul,
			Element li) {
		for (ServicewsInfo si : kind) {
			li = li.clone();
			li.attr("swsid",si.getSwsId().toString());
			String src = si.getFaceImg();
			if(StringUtil.isEmpty(src)){
			src=String.format("../%s/module-icon.svg",si.getUsePortal());
			}else{
				src=String.format("../resource/ud/%s?path=%s://system/faces&u=%s", si.getFaceImg(),si.getSwsId(),si.getOwner());
			}
			li.select("img").attr("src", src);
			li.select("p").html(si.getName());
			if (si.getDescription() != null)
				li.attr("title", si.getDescription());

			ul.appendChild(li);
		}

	}

	protected SosUserInfo getSwsList(String owner) throws CircuitException {
		// 认证成功则查找默认视窗，如果有则
		Frame frame = new Frame("getAllSws /sws/owner sos/1.0");
		frame.parameter("userCode", owner);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state == 404) {
				return null;
			}
			if (state != 200) {
				throw new CircuitException("503",
						String.format("获取用户%s的视窗失败", owner));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json, SosUserInfo.class);

		}
		return null;
	}

}
