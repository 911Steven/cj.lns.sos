package cj.lns.chip.sos.website.portal.component;

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

@CjService(name = "/market/findMenu.service")
public class FindMenu implements IComponent {
	@CjServiceRef(refByName = "marketCenter")
	MarketCenter market;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String categoryId = frame.parameter("category");
		String menuId = frame.parameter("menuId");
		String provider = frame.parameter("provider");

		IDeviceMarket device = market.device(frame);
		IMenuFair fair = device.getMenuFair();

		IMenuCategory ac = fair.category(categoryId);
		ISubject subject = ISubject.subject(frame);
		MenuSO menu = ac.findMenu(subject, provider, menuId);
		if (menu == null) {
			circuit.status("404");
			circuit.message("未找到菜单");
		} else {
			String json = new Gson().toJson(menu);
			circuit.content().writeBytes(json.getBytes());
		}

	}

}
