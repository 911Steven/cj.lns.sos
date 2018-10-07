package cj.lns.chip.sos.service.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;

@CjService(name = "dataSourceAdapter")
public class DataSourceAdapter implements IDataSourceAdapter, IServiceAfter {
	private static DataSourceAdapter dataSourceAdapter;
	private Map<String, IDataSourceProvider> providers;// provider name
	private Map<String, DataSource> cache;// jdbcUrl
	@CjServiceRef(refByName = "configableDataSourceStragety")
	IAccessDataSourceStrategy strategy;
	
	public DataSourceAdapter() {
		cache = new HashMap<String, DataSource>();
		dataSourceAdapter=this;
	}

	@Override
	public void onAfter(IServiceSite site) {
		providers = new HashMap<String, IDataSourceProvider>();
		ServiceCollection<IDataSourceProvider> col = site
				.getServices(IDataSourceProvider.class);
		for (IDataSourceProvider sp : col) {
			CjService def = sp.getClass().getAnnotation(CjService.class);
			providers.put(def.name(), sp);
		}

	}

	public static IDataSourceAdapter instance() {
		return dataSourceAdapter;
	}

	/*
	 * 系统管理员可控制为哪个地址，什么驱动，哪个用户使用哪个适配器，而ds的使用方不必关心这个问题，他们只需要用即可。
	 */
	public DataSource get(String requestPluginId, String jdbcUrl,
			String jdbcDriver, String jdbcUser, String jdbcPass) {
		if (cache.containsKey(jdbcUrl)) {
			return cache.get(jdbcUrl);
		}
		if (strategy == null) {
			strategy = new DataSourceStragety();
			CJSystem.current().environment().logging()
					.warn("数据源使用策略未定义，系统采用默认的数据源访问策略");
		}
		String providerType = strategy.useProvider(requestPluginId, jdbcUrl,
				jdbcDriver, jdbcUser,jdbcPass);
		if (!providers.containsKey(providerType)) {
			throw new EcmException("不支持的数据源提供器:" + providerType);
		}
		DataSource ds;
		try {
			ds = providers.get(providerType).get(jdbcUrl, jdbcDriver, jdbcUser,
					jdbcPass);
			cache.put(jdbcUrl, ds);
			return ds;
		} catch (Exception e) {
			throw new EcmException(e);
		}

	}

	class DataSourceStragety implements IAccessDataSourceStrategy {
		@Override
		public String useProvider(String requestPluginId, String jdbcUrl,
				String jdbcDriver, String jdbcUser,String jdbcPassword) {
			return "c3p0DataSourceProvider";
		}
	}
}
