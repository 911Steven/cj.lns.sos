package cj.lns.chip.sos.website.market.app.cyberport.component;

import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.service.sws.ServicewsSummary;
import cj.lns.chip.sos.website.framework.Face;
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

@CjService(name = "/popup/cyberportPopup.html")
public class CyberportPopupComponent implements IComponent {
	@CjServiceRef(refByName = "FetchServciewsInfo")
	FetchServciewsInfo dao;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// 作用适配客户端类型，并读取场景，为终端插件设画布
		IServicewsContext ctx = IServicewsContext.context(frame);
		if (ctx.isOwner()) {
			printCurrentSwsCyberport(frame, circuit, plug, ctx);
		} else {
			printMySwsList(frame, circuit, plug, ctx);
		}

	}

	// 打印我的视窗列表（有通知消息提示）及访问的目标视窗信息
	private void printMySwsList(Frame frame, Circuit circuit, IPlug plug,
			IServicewsContext ctx) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/popup/cyberport-swslist.html",
				m.site().contextPath(), "utf-8");
		Face face=ctx.face();
		Element he=doc.select(".visit > .v-list > li.he").first();
		String src=String.format("./resource/ud/%s?path=home://system/img/faces/&u=%s", face.getHead(),ctx.owner());
		he.select(">p").html(ctx.owner());
		he.select(">img").attr("src",src);
		Element hesws=doc.select(".visit > .v-list > li.hesws").first();
		hesws.select(">p").html(ctx.prop("sws-name"));
		hesws.attr("title",ctx.prop("sws-desc")==null?"":ctx.prop("sws-desc"));
		src=String.format("./resource/ud/%s?path=%s://system/faces/&u=%s", ctx.prop("sws-img"),ctx.swsid(),ctx.owner());
		hesws.select(">img").attr("src",src);
		
		Element ul=doc.select(".cyb-box > .sws-box").first();
		Element oli=ul.select(">li").first().clone();
		ul.empty();
		SosUserInfo si = dao.getUserServicewsSummaries(ctx.visitor().principal(), m);
		List<ServicewsInfo> list = si.getSwsList();
		for (ServicewsInfo sws : list) {
			Element li=oli.clone();
			src = "#";
			if (sws.getFaceImg() == null) {
				src = String.format("./%s/module-icon.svg", sws.getUsePortal());
			} else {
				src = String.format("./resource/ud/%s?path=%s://system/faces/&u=%s",
						sws.getFaceImg(), sws.getSwsId().toString(),
						sws.getOwner());
			}
			li.select(">img").attr("src", src);
			li.select(">p").html(sws.getName());
			li.attr("title", sws.getDescription());
			li.attr("swsid", String.valueOf(sws.getSwsId()));
			ul.appendChild(li);
		}
		
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void printCurrentSwsCyberport(Frame frame, Circuit circuit,
			IPlug plug, IServicewsContext ctx) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/popup/cyberport-popup.html",
				m.site().contextPath(), "utf-8");

		ServicewsSummary info = dao.getServicewsSummary(ctx.swsid(), m);
		Elements es = doc.select(".cyberport-box>.content>.left>.cyb-sws");
		Element e = es.first();
		e.attr("swsid", String.valueOf(ctx.swsid()));
		String src = "#";
		if (info.getFaceImg() == null) {
			src = String.format("./%s/module-icon.svg", info.getPortalId());
		} else {
			src = String.format("./resource/ud/%s?path=%s://system/faces/&u=%s",
					info.getFaceImg(), info.getSwsid().toString(),
					ctx.visitor().principal());
		}
		e.select(">.session>img").attr("src", src);
		e.select(">.session>.title").html(info.getSwsName());
		e.select(">.session>img").attr("title", info.getSwsDesc());
		SosUserInfo si = dao.getUserServicewsSummaries(ctx.owner(), m);
		Element ul = doc.select(".cyberport-box>.bottom>ul.others").first();
		Element li = ul.select(">li.sws").first();
		li = li.clone();
		ul.empty();
		List<ServicewsInfo> list = si.getSwsList();
		for (ServicewsInfo sws : list) {
			if (sws.getSwsId().equals(info.getSwsid()))
				continue;
			printSws(sws, li, ul);
		}
		InboxSessionViewer viewer = dao.getUserSessions(ctx.owner(),
				ctx.swsid(), m);
		Element appUl = doc.select("#cyb-apps").first();
		dao.printViewer(appUl, viewer);
		Map<String, Long> total = dao.getMessageTotal(ctx.owner(), ctx.swsid(),
				m);
		dao.printMessageTotal(
				doc.select(".cyberport-box>.content>.left>.cyb-sws>ul.tips")
						.first(),
				total);
		if (ctx.visitor().containsRole("guestUsers")) {
			doc.select(".bottom > ul.desc > li").first()
					.html(String.format("公众 的信箱", ctx.visitor().principal()));
		} else {
			doc.select(".bottom > ul.desc > li").first()
					.html(String.format("%s 的信箱", ctx.visitor().principal()));
		}
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void printSws(ServicewsInfo sws, Element li, Element ul) {
		li = li.clone();
		li.attr("title", sws.getDescription());
		li.attr("swsid", String.valueOf(sws.getSwsId()));
		li.select(">p").first().html(sws.getName());
		String src = "#";
		if (sws.getFaceImg() == null) {
			src = String.format("./%s/module-icon.svg", sws.getUsePortal());
		} else {
			src = String.format("./resource/ud/%s?path=%s://system/faces/&u=%s",
					sws.getFaceImg(), sws.getSwsId().toString(),
					sws.getOwner());
		}
		li.select(">img").attr("src", src);
		li.select(">.tips").empty();
		li.select(">.tips").attr("style", "display:none");
		ul.appendChild(li);
	}

}
