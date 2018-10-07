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

@CjService(name = "/public/register.html")
public class RegisterPage extends Page {

	@Override
	protected void doPage(Frame frame, Circuit circuit,IPlug plug, PageContext ctx)
			throws CircuitException {
		
		Document doc = ctx.html(frame.relativePath());
		List<ServicewsInfo> list = getSwsTemplateList();
		Elements es = doc.select(".c-left > ul[name=swsTemplateList]");
		/*
		 * <li swsId="8882323933"><img src="../img/myhome.svg"><br>
									<span class="swsName">动客视窗</span><span class="swsSelect"><input
										type="checkbox"></span></li>
		 */
		es.empty();
		String html = "";
		for (ServicewsInfo si : list) {
			html += String.format(
					"<li swsId=\"%s\" desc='%s'>" + "<img src=\"../img/myhome.svg\"><br>"
							+ "<span class=\"swsName\">%s</span>"
							+ "<span class=\"swsSelect\">"
							+ "<input type=\"checkbox\">" + "</span>" + "</li>",
					si.getSwsId(),si.getDescription(), si.getName());
		}
		es.append(html);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	protected List<ServicewsInfo> getSwsTemplateList() throws CircuitException {
		Frame frame = new Frame("getAllSwsTemplate /framework/info/ sos/1.0");
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json,
					new TypeToken<List<ServicewsInfo>>() {
					}.getType());

		}
		return new ArrayList<>();
	}
}
