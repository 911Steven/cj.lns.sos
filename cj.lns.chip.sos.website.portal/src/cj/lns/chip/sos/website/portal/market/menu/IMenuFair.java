package cj.lns.chip.sos.website.portal.market.menu;

import java.util.List;

import cj.lns.chip.sos.website.IMenuProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.graph.CircuitException;

public interface IMenuFair  {
	IMenuCategory category(String category);
	public boolean containsCategory(String category);
	public String[] enumCategory();
	void loadCategoryConfig(IServiceosWebsiteModule chipm)throws CircuitException;
	void addMenuProviders(String name, IServiceosWebsiteModule cm, List<IMenuProvider> list);
}
