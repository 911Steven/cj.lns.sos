package cj.lns.chip.sos.website.market.app.provider;

import java.util.List;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;

/**
 * 数据库配置的应用提供器
 * 
 * <pre>
 * ~ 必须声明为服务
 * ~ 支持在数据库中配置应用
 * </pre>
 * 
 * @author carocean
 *
 */
public abstract class AppDatabaseProvider
		implements IAppProvider, IServiceAfter {
	@Override
	public void onAfter(IServiceSite site) {
		

	}

	/**
	 * 配置文件
	 * 
	 * <pre>
	 * 默认文件在 resources/fair-app.plugin.json
	 * </pre>
	 * 
	 * @return
	 */
	protected String getConfigJsonFile() {
		return "/fair-app.plugin.json";
	}

	@Override
	public String category() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppSO> getApps(ISubject subject,IServicewsContext sws) {
		// TODO Auto-generated method stub
		return null;
	}

}
