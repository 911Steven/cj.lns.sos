package cj.lns.chip.sos.website.portal.market;

import cj.lns.chip.sos.website.portal.market.app.AppFair;
import cj.lns.chip.sos.website.portal.market.app.IAppFair;
import cj.lns.chip.sos.website.portal.market.menu.IMenuFair;
import cj.lns.chip.sos.website.portal.market.menu.MenuFair;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletFair;
import cj.lns.chip.sos.website.portal.market.portlet.PortletFair;

public class DeviceMarket implements IDeviceMarket{
	private IAppFair appFair;
	private IMenuFair menuFair;
	private IPortletFair portletFair;
	public DeviceMarket() {
		appFair=new AppFair();
		menuFair=new MenuFair();
		portletFair=new PortletFair();
	}
	public IAppFair getAppFair() {
		return appFair;
	}
	public IMenuFair getMenuFair() {
		return menuFair;
	}
	public IPortletFair getPortletFair() {
		return portletFair;
	}
}
