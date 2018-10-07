package cj.lns.chip.sos.website.portal.component;

import java.util.List;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.framework.IServicewsContext;
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

@CjService(name = "/market/appFair.service")
public class AppFair implements IComponent {
	@CjServiceRef(refByName = "marketCenter")
	MarketCenter market;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String categoryId = frame.parameter("category");
		
		IDeviceMarket device = market.device(frame);
		IAppFair fair = device.getAppFair();

		IAppCategory ac = fair.category(categoryId);
		circuit.attribute("category-name",ac.getCategoryName());
		circuit.attribute("category-desc",ac.getCategoryDesc());
		circuit.attribute("category-icon",ac.getCategoryIcon());
		ISubject subject=ISubject.subject(frame);
		IServicewsContext sws=IServicewsContext.context(frame);
		List<AppSO> apps=ac.getApps(subject,sws);
		
		String json=new Gson().toJson(apps);
		
		circuit.content().writeBytes(json.getBytes());
	}

}
