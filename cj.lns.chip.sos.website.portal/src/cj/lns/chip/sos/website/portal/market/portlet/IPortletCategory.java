package cj.lns.chip.sos.website.portal.market.portlet;

import java.util.List;

import cj.lns.chip.sos.website.IPortletProvider;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.ultimate.IDisposable;

public interface IPortletCategory extends IDisposable {
	/**
	 * 获取指定提供器的宿主模块
	 * <pre>
	 *
	 * </pre>
	 * @param provider
	 * @return
	 */
	String getHost(IPortletProvider provider);
	String getCategoryIcon();
	void setCategoryIcon(String icon);
	String getCategoryDesc();
	public String getCategoryName();

	List<PortletSO> getPortlets(ISubject subject);
	void addPortletProvider(String hostId,IPortletProvider ap);

	void setCategoryName(String cname);

	void setCategoryDesc(String cdesc);
	/**
	 * 
	 * <pre>
	 *
	 * </pre>
	 * @param subject
	 * @param hostId 是芯片标识，即hostId
	 * @param portletId
	 */
	PortletSO findPortlet(ISubject subject, String hostId, String portletId);
}
