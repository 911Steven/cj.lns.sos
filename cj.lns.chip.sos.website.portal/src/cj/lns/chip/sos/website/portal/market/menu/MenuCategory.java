package cj.lns.chip.sos.website.portal.market.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.IMenuProvider;
import cj.lns.chip.sos.website.MenuSO;
import cj.lns.chip.sos.website.framework.ISubject;

class MenuCategory implements IMenuCategory {
	
	List<IMenuProvider> providers;
	Map<Integer, String> host;// 提供者所在的host,key:为提供器的hashCode
	private String categoryName;
	private String categoryDesc;
	private String categoryIcon;

	public MenuCategory() {
		providers = new ArrayList<>();
		host = new HashMap<>();
	}

	@Override
	public MenuSO findMenu(ISubject subject, String hostId, String menuId) {
		for(IMenuProvider p:providers){
			if(hostId.equals(host.get(p.hashCode()))){
				return p.findMenu(subject,menuId);
			}
		}
		return null;
	}
	@Override
	public List<MenuSO> getMenus(ISubject subject) {
		List<MenuSO> menus = new ArrayList<>();
		for (IMenuProvider p : providers) {
			List<MenuSO> gets = p.getMenus(subject);
			if (gets != null && !gets.isEmpty())
				menus.addAll(gets);
		}
		return menus;
	}
	public String getHost(IMenuProvider provider) {
		return host.get(provider.hashCode());
	}
	@Override
	public void addMenuProvider(String hostId, IMenuProvider ap) {
		providers.add(ap);
		host.put(ap.hashCode(), hostId);
	}

	@Override
	public void dispose() {
		providers.clear();
		host.clear();
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public String getCategoryName() {
		return categoryName;
	}

	@Override
	public void setCategoryName(String cname) {
		this.categoryName = cname;
	}

	@Override
	public void setCategoryDesc(String cdesc) {
		this.categoryDesc = cdesc;
	}

	@Override
	public String getCategoryIcon() {
		// TODO Auto-generated method stub
		return categoryIcon;
	}

	@Override
	public void setCategoryIcon(String icon) {
		// TODO Auto-generated method stub
		this.categoryIcon=icon;
	}

}
