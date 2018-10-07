package cj.lns.chip.sos.website.security.component;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.security.Category;
import cj.lns.chip.sos.website.security.ISecurityCenter;
import cj.lns.chip.sos.website.security.ISecurityResourceImpl;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/")
public class PortalComponent implements IComponent {
	@CjServiceRef(refByName = "securityCenter")
	ISecurityCenter center;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Document doc = m.context().html("/security-portal.html",
				m.site().contextPath(), "utf-8");
		Element spliter = doc.select(".security>.content-panel>.sp-panel")
				.first().clone();
		Element list = doc.select(".security>.content-panel>ul.list").first()
				.clone();
		Element item = list.select("li.item").first().clone();
		Element content = doc.select(".security>.content-panel").first();
		content.empty();
		String[] categories = center.enumCategory();
		for (String kind : categories) {
			Category c = center.category(kind);
			Element sp = spliter.clone();
			sp.html(c.getCategoryName());
			sp.attr("title",c.getDesc());
			content.appendChild(sp);
			list = list.clone();
			list.empty();
			for (ISecurityResourceImpl impl : c.getResourceImpls()) {
				ISecurityObject so = impl.root();
				if (so == null) {
					continue;
				}
				if (!impl.isRootVisible(so, ISubject.subject(frame),
						IServicewsContext.context(frame))) {
					continue;
				}
				item = item.clone();
				String viewResource = String.format(
						"./security/securityobject-view.html?rootResourceId=%s&rootValueId=%s&resourceId=%s&valueId=%s",
						so.resourceId(), so.valueId(), so.resourceId(),
						so.valueId());
				item.attr("openContent", viewResource);
				item.select("img").attr("src", so.valueIcon());
				item.select(".main-panel>div.valueName").html(so.valueName());
				item.select(".main-panel>div.resourceName")
						.html(so.resourceName());
				list.appendChild(item);
			}
			content.appendChild(list);
		}

		circuit.content().writeBytes(doc.toString().getBytes());
	}

}
