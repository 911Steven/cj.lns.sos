package cj.lns.chip.sos.website.portal.component;

import java.util.List;

import cj.lns.chip.sos.website.MenuSO;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.portal.MarketCenter;
import cj.lns.chip.sos.website.portal.market.IDeviceMarket;
import cj.lns.chip.sos.website.portal.market.menu.IMenuCategory;
import cj.lns.chip.sos.website.portal.market.menu.IMenuFair;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/market/menuFair.service")
public class MenuFair implements IComponent {
	@CjServiceRef(refByName = "marketCenter")
	MarketCenter market;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String categoryId = frame.parameter("category");
		
		IDeviceMarket device = market.device(frame);
		IMenuFair fair = device.getMenuFair();

		IMenuCategory ac = fair.category(categoryId);
		circuit.attribute("category-name",ac.getCategoryName());
		circuit.attribute("category-desc",ac.getCategoryDesc());
		circuit.attribute("category-icon",ac.getCategoryIcon());
		ISubject subject=ISubject.subject(frame);
		List<MenuSO> menus=ac.getMenus(subject);
		
		String json=new Gson().toJson(menus);
		
		circuit.content().writeBytes(json.getBytes());
	}

}
