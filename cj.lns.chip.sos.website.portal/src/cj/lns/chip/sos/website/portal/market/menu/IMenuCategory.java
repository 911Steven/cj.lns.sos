package cj.lns.chip.sos.website.portal.market.menu;

import java.util.List;

import cj.lns.chip.sos.website.IMenuProvider;
import cj.lns.chip.sos.website.MenuSO;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.ultimate.IDisposable;

public interface IMenuCategory extends IDisposable{
	/**
	 * 获取指定提供器的宿主模块
	 * <pre>
	 *
	 * </pre>
	 * @param provider
	 * @return
	 */
	String getHost(IMenuProvider provider);
	String getCategoryIcon();
	void setCategoryIcon(String icon);
	String getCategoryDesc();
	public String getCategoryName();

	List<MenuSO> getMenus(ISubject subject);
	void addMenuProvider(String hostId,IMenuProvider ap);

	void setCategoryName(String cname);

	void setCategoryDesc(String cdesc);
	/**
	 * 
	 * <pre>
	 *
	 * </pre>
	 * @param subject
	 * @param hostId 是芯片标识，即hostId
	 * @param menuId
	 */
	MenuSO findMenu(ISubject subject, String hostId, String menuId);
}
