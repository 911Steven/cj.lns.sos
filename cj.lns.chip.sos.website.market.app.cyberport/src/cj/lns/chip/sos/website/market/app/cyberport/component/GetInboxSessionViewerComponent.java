package cj.lns.chip.sos.website.market.app.cyberport.component;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.market.app.cyberport.services.FetchServciewsInfo;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.sns.mailbox.viewer.InboxSessionViewer;

@CjService(name = "/popup/getInboxSessionViewer.html")
public class GetInboxSessionViewerComponent implements IComponent {
	@CjServiceRef(refByName = "FetchServciewsInfo")
	FetchServciewsInfo dao;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// 作用适配客户端类型，并读取场景，为终端插件设画布
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/popup/cyberport-popup.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext ctx = IServicewsContext.context(frame);
		InboxSessionViewer viewer = dao.getUserSessions(ctx.owner(),
				ctx.swsid(), m);
		Element appUl = doc.select("#cyb-apps").first();
		dao.printViewer(appUl, viewer);
		Map<String, Long> total = dao.getMessageTotal(ctx.owner(),
				ctx.swsid(), m);
		Element hidden=new Element(Tag.valueOf("input"), "");
		hidden.attr("id","report_msg");
		hidden.attr("type","hidden");
		hidden.attr("inbox",String.valueOf(total.get("inbox")));
		hidden.attr("outbox",String.valueOf(total.get("outbox")));
		hidden.attr("drafts",String.valueOf(total.get("drafts")));
		hidden.attr("trash",String.valueOf(total.get("trash")));
		appUl.appendChild(hidden);
		circuit.content().writeBytes(appUl.html().getBytes());
	}



}
