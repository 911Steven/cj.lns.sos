package cj.lns.chip.sos.website.portal.market;

import cj.lns.chip.sos.website.portal.market.app.IAppFair;
import cj.lns.chip.sos.website.portal.market.menu.IMenuFair;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletFair;
/**
 * 面向终端的子市场
 * <pre>
 * · 以抽象工厂模式实现对三类集市的构建
 * </pre>
 * @author carocean
 *
 */
public interface IDeviceMarket {
	public IAppFair getAppFair();
	public IMenuFair getMenuFair();
	public IPortletFair getPortletFair();
}
