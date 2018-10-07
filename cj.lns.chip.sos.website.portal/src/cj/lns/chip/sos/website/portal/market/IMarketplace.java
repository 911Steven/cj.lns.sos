package cj.lns.chip.sos.website.portal.market;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;

/**
 * 市场
 * <pre>
 * 用法：
 * · 框架必须派生此类并声明为服务，才能拥有市场
 * 
 * 注意：
 * ~ IMarketplace仅表示一个portal的元件市场，如需要定位到一个市场或获取所有市场，需要使用marketCenter服务。
 * </pre>
 * @author carocean
 *
 */
public interface IMarketplace {
	boolean contains(String device);
	
	public IDeviceMarket deviceMarket(String device);

	public String[] enumDeviceMarket();
	/**
	 * 扫描整个市场，作用是发现各插件中的提供器
	 * <pre>
	 *
	 * </pre>
	 * @param m  框架实现模块
	 */
	public void scans(IServiceosWebsiteModule m);
}
