package cj.lns.chip.sos.website.portal.market.app;

import java.util.List;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.ultimate.IDisposable;
/**
 * 表示一个分类
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
public interface IAppCategory extends IDisposable{
	/**
	 * 获取指定提供器的宿主模块
	 * <pre>
	 *
	 * </pre>
	 * @param provider
	 * @return
	 */
	String getHost(IAppProvider provider);
	String getCategoryIcon();
	void setCategoryIcon(String icon);
	String getCategoryDesc();
	public String getCategoryName();

	void addAppProvider(String hostId,IAppProvider ap);

	void setCategoryName(String cname);

	void setCategoryDesc(String cdesc);
	/**
	 * 
	 * <pre>
	 *
	 * </pre>
	 * @param subject
	 * @param hostId 是芯片标识，即hostId
	 * @param appId
	 */
	AppSO findApp(ISubject subject, String hostId, String appId);
	List<AppSO> getApps(ISubject subject, IServicewsContext sws);
}
