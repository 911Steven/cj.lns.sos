package cj.lns.chip.sos.website.portal.component;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.portal.MarketCenter;
import cj.lns.chip.sos.website.portal.market.IDeviceMarket;
import cj.lns.chip.sos.website.portal.market.app.IAppCategory;
import cj.lns.chip.sos.website.portal.market.app.IAppFair;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/market/findApp.service")
public class FindApp implements IComponent {
	@CjServiceRef(refByName = "marketCenter")
	MarketCenter market;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String categoryId = frame.parameter("category");
		String appId = frame.parameter("appId");
		String provider = frame.parameter("provider");

		IDeviceMarket device = market.device(frame);
		IAppFair fair = device.getAppFair();

		IAppCategory ac = fair.category(categoryId);
		ISubject subject = ISubject.subject(frame);
		AppSO app = ac.findApp(subject, provider, appId);
		if (app == null) {
			circuit.status("404");
			circuit.message("未找到应用");
		} else {
			String json = new Gson().toJson(app);
			circuit.content().writeBytes(json.getBytes());
		}

	}

}
