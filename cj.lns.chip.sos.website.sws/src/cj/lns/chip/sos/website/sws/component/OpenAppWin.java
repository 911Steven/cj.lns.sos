package cj.lns.chip.sos.website.sws.component;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.website.AppMenu;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppMenuLoader;
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
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.WebUtil;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

@CjService(name = "/openWin/")
public class OpenAppWin implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext ctx = IServicewsContext.context(frame);
		String paramStr = new String(frame.content().readFully());
		Map<String, Object> map = WebUtil.parserParam(paramStr);

		String cmd = (String) map.get("valueId");
		String valueId = cmd;
		String querystring = "";
		if (cmd.startsWith("app://")) {// 物理标识
			int pos = cmd.indexOf("?");
			if (pos > 0) {// 有querystring
				valueId = cmd.substring(6, pos);
				querystring = cmd.substring(pos + 1, cmd.length());
			} else {
				valueId = cmd.substring(6, cmd.length());
			}
		} else if (cmd.startsWith("appi://")) {// 应用的逻辑标识
			int pos = cmd.indexOf("?");
			if (pos > 0) {// 有querystring
				valueId = cmd.substring(7, pos);
				querystring = cmd.substring(pos + 1, cmd.length());
			} else {
				valueId = cmd.substring(7, cmd.length());
			}
			valueId = getAppPhyId(valueId, m, ctx);
			if (valueId == null) {
				throw new CircuitException("404",
						String.format("应用不存在：cmd=%s", cmd));
			}
		}
		frame.parameter("resourceId", (String) map.get("resourceId"));
		frame.parameter("valueId", valueId);// 再次覆盖原物理标识
		String finder = "/security/resourceFinder.service";
		if (frame.containsQueryString()) {
			finder = String.format("%s?%s", finder, frame.queryString());
		}
		frame.url(finder);
		frame.parameter("rootResourceId", "tool");
		String tool = (String) map.get("tool");
		frame.parameter("rootValueId", tool);
		m.site().out().flow(frame, circuit);
		String json = new String(circuit.content().readFully());
		AppSO so = new Gson().fromJson(json, AppSO.class);// 由于无论发布还是应用等资源均是从应用资源中派生的，所以可统一用应用资源代替
		if(so==null){
			throw new CircuitException("404", "请求的应用不存在："+frame.url());
		}
		if(!StringUtil.isEmpty(querystring)){//此处的querystring是仅当在以应用的物理或逻辑标识打开时才被求出
			if(so.getCommand()!=null&&so.getCommand().contains("?")){
				so.setCommand(String.format("%s&%s", so.getCommand(),querystring));
			}else{
				so.setCommand(String.format("%s?%s", so.getCommand(),querystring));
			}
		}
		so.setLoader((IAppMenuLoader) circuit.attribute("appmenus-loader"));
		Document doc = m.context().html("/windows-local.html",
				m.site().contextPath(), "utf-8");
		if (!ctx.isOwner()) {
			doc.select(".windows > .titleBar > .provider-view").attr("style",
					"display:none;");
		}
		render(doc, so, m, frame, circuit);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private String getAppPhyId(String id/*逻辑标识*/, IServiceosWebsiteModule m,
			IServicewsContext ctx) throws CircuitException {
		Frame frame = new Frame("findPhyIdByLogicId /sws/market/app/ sos/1.0");
		frame.parameter("swsId", ctx.swsid());
		frame.parameter("code", id);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		m.site().out().flow(frame, circuit);
		IFlowContent fc = circuit.content();
		if (fc.readableBytes() > 0) {
			Frame back = new Frame(fc.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException("503",
						String.format("远程错误：%s", back.head("message")));
			}
			return back.head("phyid");
		}
		return null;
	}

	private void render(Document doc, AppSO so, IServiceosWebsiteModule m,
			Frame frame, Circuit circuit) throws CircuitException {
		Element title = doc.select(".windows>.titleBar").first();
		Element menu = doc.select(".windows > .titleBar > .menu > ul").first();
		Element menuli = menu.select(">li").first().clone();
		menu.empty();
		if (so.getPortal() != null) {
			if (so.getPortal().getLeft() != null)
				title.attr("pleft", so.getPortal().getLeft());
			if (so.getPortal().getRight() != null)
				title.attr("pright", so.getPortal().getRight());
		}
		title.select("a>span").html(so.getName());
		if (!StringUtil.isEmpty(so.getIcon()))
			title.select("a>img").attr("src", so.getIcon());
		String url = so.getCommand();
		if (StringUtil.isEmpty(url))
			return;
		if (url.startsWith("./")) {
			url = url.substring(1, url.length());
		}
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		ISubject subject = ISubject.subject(frame);
		IServicewsContext ctx = IServicewsContext.context(frame);
		so.loadMenus(subject, ctx);
		for (AppMenu am : so.getMenus()) {
			menuli = menuli.clone();
			menuli.select(">p").html(am.getName());
			menuli.attr("cmd", am.getCmd());
			// if(!StringUtil.isEmpty(am.getId())){
			// menuli.attr("id",am.getId());
			// }
			if (StringUtil.isEmpty(am.getIcon()) || "#".equals(am.getIcon())) {
				menuli.select(">img").attr("style", "display:none;");
			} else {
				menuli.select(">img").attr("src", am.getIcon());
			}
			menu.appendChild(menuli);
		}
		frame.url(url);
		// Frame f=new Frame(String.format("get %s http/1.1",url));
		m.site().out().flow(frame, circuit);
		if (circuit.content().readableBytes() > 0) {
			String html = new String(circuit.content().readFully());
			doc.select(".windows>.win-content").html(html);
		}

	}

}
