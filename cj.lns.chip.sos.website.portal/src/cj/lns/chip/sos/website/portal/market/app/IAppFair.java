package cj.lns.chip.sos.website.portal.market.app;

import java.util.List;

import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.graph.CircuitException;

public interface IAppFair {
	IAppCategory category(String category);
	public boolean containsCategory(String category);
	public String[] enumCategory();
	/**
	 * 
	 * <pre>
	 *
	 * </pre>
	 * @param moduleName
	 * @param cm 提供器所在的模块
	 * @param providers
	 */
	void addAppProviders(String moduleName,IServiceosWebsiteModule cm, List<IAppProvider> providers);
	void loadCategoryConfig(IServiceosWebsiteModule m) throws CircuitException;
}
