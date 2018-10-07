package cj.lns.chip.sos.website.sws.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.sws.ServicewsContext;
import cj.lns.chip.sos.website.sws.service.IAuthAnswerService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.HttpFrame;
import cj.studio.ecm.net.web.WebUtil;
import cj.studio.ecm.plugins.moduleable.IModuleContext;

@CjService(name = "/authAnswer.html")
public class AuthAnswerComponent implements IComponent {
	@CjServiceRef(refByName = "authAnswerService")
	IAuthAnswerService answer;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		if ("render".equals(frame.parameter("action"))) {
			render(frame,circuit, plug);
		} else {
			part(frame, plug);
		}

	}

	// 客户端或其它模块发起的请求
	private void part(Frame frame, IPlug plug) throws CircuitException {

		String params = new String(frame.content().readFully());
		Map<String, Object> map = WebUtil.parserParam(params);
		if (!"auth".equals(frame.parameter("action"))) {
			return;
		}
		answer.auth((String)map.get("askId"), (String)map.get("answer"));
		Face face=new Face("","","","",(byte)0);
		String ownerCode="";
		byte level=3;
		// 认证之后设置上下文
		/*需要视窗认证的一定非持有者，持有者进视窗全权进入，不需认证*/
		Map<String,String> props=new HashMap<>();
		IServicewsContext sws = new ServicewsContext(ownerCode,(String)map.get("swsid"),level,(String)map.get("swstid"),
				ISubject.subject(frame),face,props);
		HttpFrame f = (HttpFrame) frame;
		f.session().attribute(IServicewsContext.KEY_SWS_CONTEXT_IN_SESSION,
				sws);
	}

	private void render(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IModuleContext ctx = m.context();
		Document doc = ctx.html("/auth-answer.html", m.site().contextPath(),
				"utf-8");
		List<AuthAskInfo> list = answer.getAskList(frame.parameter("swsid"),
				frame.parameter("permissionId"));
		Elements es = doc.select("#ask");
		Element e = es.first();
		e.attr("swsid", frame.parameter("swsid"));
		e.empty();
		for (AuthAskInfo ask : list) {
			e.append(String.format("<option value='%s'>%s</option>",
					ask.getId(), ask.getAsk()));
		}

		circuit.content().writeBytes(doc.toString().getBytes());

	}

}
