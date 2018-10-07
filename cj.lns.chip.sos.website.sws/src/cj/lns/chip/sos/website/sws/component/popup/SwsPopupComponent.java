package cj.lns.chip.sos.website.sws.component.popup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.service.sws.ServicewsSummary;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.util.StringUtil;

@CjService(name = "/components/swsPopup.html")
public class SwsPopupComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/components/sws-popup.html",
				m.site().contextPath(), "utf-8");
		IServicewsContext context = IServicewsContext.context(frame);
		ServicewsSummary summary = sws.getServicewsSummary(context.swsid(), m);
		renderSummary(doc, summary, m,context);
		circuit.content().writeBytes(doc.toString().getBytes());
	}

	private void renderSummary(Document doc, ServicewsSummary summary,
			IServiceosWebsiteModule m, IServicewsContext ctx) {
		Element si = doc.select(".sws-panel>.sws-tabs>.sws-info").first();
		si.select("p[name]").html(summary.getSwsName());
		String src="#";
		if(summary.getFaceImg()==null){
		src=String.format("./%s/module-icon.svg", summary.getPortalId());
		}else{
			src=String.format("./resource/ud/%s?path=%s://system/faces/&u=%s", summary.getFaceImg(),summary.getSwsid().toString(),summary.getOwner().getUserCode());
		}
		si.select("img").attr("src",
				src);
		if (!StringUtil.isEmpty(summary.getSwsDesc())) {
			si.attr("title", summary.getSwsDesc());
		}

		Element owner = doc.select(".sws-panel>.sws-tabs>.sws-owner").first();
		String name = summary.getNickName();
		if (StringUtil.isEmpty(name)) {
			name = summary.getOwner().getUserCode();
		}
		owner.select("p[name]").html(name);
		owner.attr("title", String.format("账号:%s", summary.getOwner()));
		if (!StringUtil.isEmpty(summary.getHeadPic())) {
			
			src=String.format("./resource/ud/%s?path=home://system/img/faces&u=%s", ctx.face().getHead(),ctx.owner());

			owner.select("img").attr("src", src);
		}
	}

}
