package cj.lns.chip.sos.service.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import cj.studio.ecm.EcmException;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.ElementGet;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

@CjService(name = "database", isExoteric = true)
public class Database implements IDatabase, IServiceAfter {
	Map<String, DbConfig> config;

	@Override
	public void onAfter(IServiceSite site) {
		config = new HashMap<>();
		String [] arr=site.enumProperty();
		for(String key:arr){
			if(!key.startsWith("db.")){
				continue;
			}
			String excludeDb=key.substring(3, key.length());
			String dbName=excludeDb.substring(0, excludeDb.indexOf("."));
			DbConfig c=config.get(dbName);
			if(c==null){
				c=new DbConfig();
				config.put(dbName, c);
			}
			String k=excludeDb.substring(excludeDb.indexOf(".")+1, excludeDb.length());
			c.map.put(k, site.getProperty(key));
		}
	}

	@Override
	public Object getService(String serviceId) {
		
		Gson g = new Gson();
		JsonObject jobj = null;
		try {
			JsonElement ele = g.fromJson(serviceId, JsonElement.class);
			jobj = ele.getAsJsonObject();
		} catch (Exception e) {
			throw new EcmException("参数格式错误：" + e);
		}
		String requestPluginId = ElementGet
				.getJsonProp(jobj.get("requestPluginId"));
		
		
		String jdbcUrl = "";
		String jdbcDriver = "";
		String jdbcUser = "";
		String jdbcPassword = "";
		jdbcUrl = ElementGet.getJsonProp(jobj.get("jdbcUrl"));
		String dbName=jdbcUrl.substring(jdbcUrl.lastIndexOf("/"), jdbcUrl.length());
		DbConfig c=config.get(dbName);
		/*
		 * 由于在persistenceFactory中为了取单元参数，采用了两次初始化参数机制，因此第一次还是用了persistence.xml中配制单元的库
		 * 所以此方案暂时隔置，今后另外想机制绕开
		 */
		if (c!=null&&"true".equals(c.map.get("used"))) {
			jdbcUrl = c.map.get("jdbcUrl");
			jdbcDriver = c.map.get("jdbcDriver");
			jdbcUser = c.map.get("jdbcUser");
			jdbcPassword = c.map.get("jdbcPassword");
		} else {
			jdbcDriver = ElementGet.getJsonProp(jobj.get("jdbcDriver"));
			jdbcUser = ElementGet.getJsonProp(jobj.get("jdbcUser"));
			jdbcPassword = ElementGet.getJsonProp(jobj.get("jdbcPassword"));
		}

		IDataSourceAdapter dsAdapter = DataSourceAdapter.instance();
		DataSource ds = dsAdapter.get(requestPluginId, jdbcUrl, jdbcDriver,
				jdbcUser, jdbcPassword);
		return ds;
	}

	@Override
	public <T> ServiceCollection<T> getServices(Class<T> serviceClazz) {
		// TODO Auto-generated method stub
		return null;
	}
}
