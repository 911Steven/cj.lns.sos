package cj.lns.chip.sos.website.portal.market.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;

class AppCategory implements IAppCategory {
	List<IAppProvider> providers;
	Map<Integer, String> host;// 提供者所在的host,key:为提供器的hashCode
	private String categoryName;
	private String categoryDesc;
	private String categoryIcon;

	public AppCategory() {
		providers = new ArrayList<>();
		host = new HashMap<>();
	}

	public String getHost(IAppProvider provider) {
		return host.get(provider.hashCode());
	}
	@Override
	public AppSO findApp(ISubject subject, String hostId, String appId) {
		for(IAppProvider p:providers){
			if(hostId.equals(host.get(p.hashCode()))){
				return p.findApp(subject,appId);
			}
		}
		return null;
	}
	@Override
	public List<AppSO> getApps(ISubject subject,IServicewsContext sws) {
		List<AppSO> apps = new ArrayList<>();
		for (IAppProvider p : providers) {
			List<AppSO> gets = p.getApps(subject,sws);
			if (gets != null && !gets.isEmpty())
				apps.addAll(gets);
		}
		return apps;
	}

	@Override
	public void addAppProvider(String hostId, IAppProvider ap) {
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
