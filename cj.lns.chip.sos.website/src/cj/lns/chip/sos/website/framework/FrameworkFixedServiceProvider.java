package cj.lns.chip.sos.website.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cj.studio.ecm.EcmException;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

/**
 * 框架向各插件提供服务
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "frameworkFixedServiceProvider")
public class FrameworkFixedServiceProvider
		implements IServiceProvider, IServiceAfter {
	Map<String, Object> services;
	private void initDatabaseCloud(IDatabaseCloud db, IServiceSite site, String homeDir) {
		String fileName = String.format("%s%sconfig%sdatabase-cloud.properties",
				homeDir, File.separator, File.separator);
		File f = new File(fileName);
		if (!f.exists()) {
			throw new EcmException(
					String.format("lns云数据库未配置，应在应用启动目录下建立文件:%s", f));
		}
		Properties props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			props.load(in);
		} catch (IOException e) {
			throw new EcmException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		db.init(props);
	}
	@Override
	public void onAfter(IServiceSite site) {
		services = new HashMap<String, Object>();
		Object frameworkInfoReporter = site.getService("frameworkInfoReporter");
		services.put("frameworkInfoReporter", frameworkInfoReporter);
		
		IDatabaseCloud db = (IDatabaseCloud) site
				.getService("databaseCloud");
		String homeDir = site.getProperty("home.dir");
		initDatabaseCloud(db,site,homeDir);
		
		services.put("databaseCloud", db);

		SnsReceptionSelector selector = new SnsReceptionSelector();
		selector.load(site);
		services.put(ISnsReceptionSelector.KEY, selector);
		
		Object cscDeveloperFactory = site.getService("cscDeveloperFactory");
		services.put("cscDeveloperFactory", cscDeveloperFactory);
	}

	@Override
	public <T> ServiceCollection<T> getServices(Class<T> serviceClazz) {
		List<Object> list = new ArrayList<Object>();
		Collection<?> v = services.values();
		for (Object t : v) {
			if (serviceClazz.isAssignableFrom(t.getClass())) {
				list.add(t);
			}
		}
		return new ServiceCollection<T>();
	}

	@Override
	public Object getService(String serviceId) {
		return services.get(serviceId);
	}

	public Map<String, Object> services() {
		return services;
	}

	class SnsReceptionSelector implements ISnsReceptionSelector {
		private List<String> deviceUrls;
		private List<String> imUrls;
		private List<String> fsUrls;

		@Override
		public String selectDeviceUrl(int factor) {
			if (deviceUrls.isEmpty())
				return "#";
			return deviceUrls.get(factor % deviceUrls.size());
		}

		public void load(IServiceSite site) {
			String[] props = site.enumProperty();
			for (String key : props) {
				if ("reception.sns.device".equals(key)) {
					String json=site.getProperty(key);
					deviceUrls=new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
					continue;
				}
				if ("reception.sns.im".equals(key)) {
					String json=site.getProperty(key);
					imUrls=new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
					continue;
				}
				if ("reception.sns.fs".equals(key)) {
					String json=site.getProperty(key);
					fsUrls=new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
					continue;
				}
			}

		}

		@Override
		public String selectFsUrl(int factor) {
			if (fsUrls.isEmpty())
				return "#";
			return fsUrls.get(factor % fsUrls.size());
		}
		@Override
		public String selectImUrl(int factor) {
			if (imUrls.isEmpty())
				return "#";
			return imUrls.get(factor % imUrls.size());
		}

	}
}
