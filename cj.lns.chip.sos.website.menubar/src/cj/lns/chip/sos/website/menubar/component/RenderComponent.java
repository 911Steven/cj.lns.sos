package cj.lns.chip.sos.website.menubar.component;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "/")
public class RenderComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/top.menu.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext sws = IServicewsContext.context(frame);
		if (!ISubject.subject(frame).getClass().getName()
				.equals("cj.lns.chip.sos.website.sws.PublicSubject")) {
			List<ServicewsInfo> swsList = getMySwsList(sws);
			printMySwsList(frame.rootName(), doc, swsList, sws);
			// 隐藏登录按钮
			doc.select(".menu-panel > .menu-tray > ul > li[login]")
					.html("&nbsp;");
		} else {
			// 如果是游客
			doc.select(".menu-panel > .menu-tray > ul > li[logout]")
					.html("&nbsp;");
			doc.select(".menu-panel > .menu-tray > ul > li[login]")
					.attr("title", String.format("当前身份是游客：%s",
							ISubject.subject(frame).principal()));
			doc.select(".menu-panel > .menu-task > ul.m-task-right")
					.attr("style", "display:none;");
		}
		if(sws.level()<3){
			doc.select(".menu-panel > .menu-sos > ul > li[officalsite]").remove();
			doc.select(".menu-panel > .menu-task > ul.m-task-left > li.service>ul>li[common]").remove();
			if(sws.level()<2){
				doc.select(".menu-panel > .menu-sos > ul > li[basicsite]").remove();
				doc.select(".menu-panel > .menu-task > ul.m-task-left > li.service").remove();
			}else{
				doc.select(".menu-panel > .menu-sos > ul > li[basicsite]").attr("basicsite",sws.swstid());
			}
		}else{
			doc.select(".menu-panel > .menu-sos > ul > li[officalsite]").attr("officalsite",sws.swstid());
			doc.select(".menu-panel > .menu-sos > ul > li[basicsite]").attr("basicsite",sws.swstid());
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void printMySwsList(String root, Document doc,
			List<ServicewsInfo> swsList, IServicewsContext sws) {
		Element ul = doc.select(".menu-panel .downbox > div.style-logout > ul")
				.first();
		ul.attr("cpath", root);
		Element li = ul.select(">li").first().clone();
		ul.empty();
		for (ServicewsInfo si : swsList) {
			if (StringUtil.isEmpty(si.getFaceImg())) {
				si.setFaceImg("./cjdk/module-icon.svg");
			} else {
				String src = String.format(
						"./resource/ud/%s?path=%s://system/faces&u=%s",
						si.getFaceImg(), si.getSwsId().toString(),
						sws.visitor().principal());
				si.setFaceImg(src);
			}
			li = li.clone();
			li.attr("swsid", si.getSwsId().toString());
			li.select(">img").attr("src", si.getFaceImg());
			li.select(">span").html(si.getName());
			String type = "";
			switch (si.getLevel()) {
			case 0:
				type = "超级视窗";
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
			li.attr("title", type);
			ul.appendChild(li);
		}
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
