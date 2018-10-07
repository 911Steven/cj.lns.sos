package cj.lns.chip.sos.website.pages;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "/public/listServicews.html")
public class ListServicews extends Page {

	@Override
	protected void doPage(Frame frame,Circuit circuit, IPlug plug, PageContext ctx)
			throws CircuitException {
		if (!frame.containsParameter("owner")
				|| StringUtil.isEmpty(frame.parameter("owner"))) {
			String fullUrl=String.format("%s://%s/%s/public/login.html", frame.protocol().replace("/1.1", ""),frame.head("Host"),frame.rootName());
			ctx.redirect(fullUrl,circuit);
			return;
		}
		Document doc = ctx.html(frame.relativePath());
		List<ServicewsInfo> all = getSwsList(frame.parameter("owner"));
		Elements es = doc.select(".left>.who");
		es.html(frame.parameter("owner"));
		Elements ul = doc.select(".left>ul");
		ul.empty();
		for (ServicewsInfo si : all) {
			String li = String.format(
					"<li><img src=\"%s\"><br> <span  id='%s' owner='%s' root='%s'>%s</span></li>","#",si.getSwsId(),frame.parameter("owner"),frame.rootName(),si.getName());
			ul.append(li);
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	protected List<ServicewsInfo> getSwsList(String owner)
			throws CircuitException {
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
			if (state != 200) {
				throw new CircuitException("503",
						String.format("获取用户%s的视窗失败", owner));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json,
					new TypeToken<List<ServicewsInfo>>() {
					}.getType());

		}
		return new ArrayList<>();
	}
}
