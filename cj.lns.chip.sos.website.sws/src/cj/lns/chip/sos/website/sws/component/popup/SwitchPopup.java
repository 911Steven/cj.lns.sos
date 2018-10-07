package cj.lns.chip.sos.website.sws.component.popup;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.IServicews;
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
@CjService(name = "/components/switchPopup.html")
public class SwitchPopup implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc=m.context().html("/components/switchPopup.html", m.site().contextPath(), "utf-8");
		IServicewsContext sws = IServicewsContext.context(frame);
		List<ServicewsInfo> swsList = getMySwsList(sws);
		
		Element box=doc.select(".swi-box").first();
		Element ali=box.select("> a").first().clone();
		box.empty();
		for(ServicewsInfo si:swsList){
			Element a=ali.clone();
			String src=String.format("./resource/ud/%s?path=%s://system/faces&u=%s",si.getFaceImg(),si.getSwsId(),si.getOwner());
			a.select(">img").attr("src",src);
			a.select(">span").html(si.getName());
			a.attr("swsid",si.getSwsId().toString());
			String type = "";
			switch (si.getLevel()) {
			case 0:
				type = "超级视窗";
				a.select(">img").attr("src","./cjdk/module-icon.svg");
				break;
			case 1:
				type = "基础视窗";
				break;
			case 2:
				type = "公共视窗";
				break;
			case 3:
				type = "个人视窗";
				break;
			}
			a.attr("title", type);
			box.appendChild(a);
		}
		Element a=ali.clone();
		a.select(">img").attr("src","img/guangjiewang.svg");
		a.select(">span").html("视窗中心");
		a.attr("type","center");
		a.attr("goto",String.format("./auth/listServicews.html?owner=%s",sws.owner()));
		box.appendChild(a);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private List<ServicewsInfo> getMySwsList(IServicewsContext sws)
			throws CircuitException {
		String swsid = sws.swsid();
		// 认证成功则查找默认视窗，如果有则
		Frame frame = new Frame("getServicewsFace /sws/instance sos/1.0");
		frame.parameter("userCode", sws.visitor().principal());
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().site().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException("503",
						String.format("获取视窗失败：%s", swsid));
			}
			String json = new String(back.content().readFully());
			List<ServicewsInfo> list = new Gson().fromJson(json,
					new TypeToken<List<ServicewsInfo>>() {
					}.getType());

			return list;

		}
		return new ArrayList<>();
	}
}
