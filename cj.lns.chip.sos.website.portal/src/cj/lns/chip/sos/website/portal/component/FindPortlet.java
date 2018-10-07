package cj.lns.chip.sos.website.portal.component;

import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.portal.MarketCenter;
import cj.lns.chip.sos.website.portal.market.IDeviceMarket;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletCategory;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletFair;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/market/findPortlet.service")
public class FindPortlet implements IComponent {
	@CjServiceRef(refByName = "marketCenter")
	MarketCenter market;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String categoryId = frame.parameter("category");
		String letId = frame.parameter("portletId");
		String provider = frame.parameter("provider");

		IDeviceMarket device = market.device(frame);
		IPortletFair fair = device.getPortletFair();

		IPortletCategory ac = fair.category(categoryId);
		ISubject subject = ISubject.subject(frame);
		PortletSO let = ac.findPortlet(subject, provider, letId);
		if (let == null) {
			circuit.status("404");
			circuit.message("未找到栏目");
		} else {
			String json = new Gson().toJson(let);
			circuit.content().writeBytes(json.getBytes());
		}

	}

}
