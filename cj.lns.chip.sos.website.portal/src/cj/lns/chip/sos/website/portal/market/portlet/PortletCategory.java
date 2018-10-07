package cj.lns.chip.sos.website.portal.market.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.IPortletProvider;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.ISubject;

class PortletCategory implements IPortletCategory {
	List<IPortletProvider> providers;
	Map<Integer, String> host;// 提供者所在的host,key:为提供器的hashCode
	private String categoryName;
	private String categoryDesc;
	private String categoryIcon;

	public PortletCategory() {
		providers = new ArrayList<>();
		host = new HashMap<>();
	}

	public String getHost(IPortletProvider provider) {
		return host.get(provider.hashCode());
	}
	
	@Override
	public PortletSO findPortlet(ISubject subject, String hostId, String letId) {
		for(IPortletProvider p:providers){
			if(hostId.equals(host.get(p.hashCode()))){
				return p.findPortlet(subject,letId);
			}
		}
		return null;
	}
	@Override
	public List<PortletSO> getPortlets(ISubject subject) {
		List<PortletSO> lets = new ArrayList<>();
		for (IPortletProvider p : providers) {
			List<PortletSO> gets = p.getPortlets(subject);
			if (gets != null && !gets.isEmpty())
				lets.addAll(gets);
		}
		return lets;
	}

	@Override
	public void addPortletProvider(String hostId, IPortletProvider ap) {
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
