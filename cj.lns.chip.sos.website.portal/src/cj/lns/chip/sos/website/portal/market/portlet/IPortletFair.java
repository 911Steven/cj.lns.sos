package cj.lns.chip.sos.website.portal.market.portlet;

import java.util.List;

import cj.lns.chip.sos.website.IPortletProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.graph.CircuitException;

public interface IPortletFair  {
	IPortletCategory category(String category);
	public boolean containsCategory(String category);
	public String[] enumCategory();
	void loadCategoryConfig(IServiceosWebsiteModule chipm)throws CircuitException;
	void addPortletProviders(String name, IServiceosWebsiteModule cm, List<IPortletProvider> list);
}
