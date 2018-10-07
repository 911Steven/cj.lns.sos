package cj.lns.chip.sos.website.market.app.cyberport.component;

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
import cj.studio.ecm.sns.mailbox.viewer.MailboxMessageViewer;
import cj.ultimate.util.StringUtil;

@CjService(name = "/popup/getMessages.html")
public class GetMessagesComponent implements IComponent {
	@CjServiceRef(refByName = "FetchServciewsInfo")
	FetchServciewsInfo dao;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// 作用适配客户端类型，并读取场景，为终端插件设画布
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/popup/getMessages.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext ctx = IServicewsContext.context(frame);
		String action = frame.parameter("action");
		if (StringUtil.isEmpty(action)) {
			throw new CircuitException("404", "参数：action不能为空");
		}
		String skip = frame.parameter("skip");
		if(StringUtil.isEmpty(skip)){
			skip="0";
		}
		String limit = frame.parameter("limit");
		if(StringUtil.isEmpty(limit)){
			limit="2000";
		}
		MailboxMessageViewer view=dao.getAllMessage(ctx.owner(),ctx.swsid(),action,skip,limit,m);
		Element templi=doc.select(".app.msg").first();
		Element container=new Element(Tag.valueOf("ul"), "");
		dao.printMessages(action,view,templi,container,m);
		container.appendChild(doc.head().children().first());
		circuit.content().writeBytes(container.html().getBytes());
	}

}
