package cj.lns.common.sos.service.moduleable;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import cj.lns.chip.sos.service.framework.IPersistenceFactory;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IChip;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.IServiceSetter;
import cj.studio.ecm.annotation.CjService;
/**
 * 持久模块
 * <pre>
 * 它表示当前芯片在sos中的持久功能
 * －持久模块是外部服务，由sos框架注入db中心服务
 * －持久模块缓冲当前模块的实体工厂
 * －持久模块向db中心发出连接请求
 * －持久模块必须内嵌到使用它的芯片中
 * －持久模块必须在芯片的assembly.json中的scans中设定cj.lns.common.sos.service.persistence包路径
 * －持久模块可被sos持久器获取到，开发者也可在其芯片中直接注入此服务，服务名为：persistenceModule
 * </pre>
 * @author carocean
 *
 */
@CjService(name = "persistenceFactory")
public class PersistenceFactory implements IPersistenceFactory,
		IServiceSetter {
	IServiceProvider sp;
	Map<String, EntityManagerFactory> cache;// persistenceUnitName
	private String chipId;

	public PersistenceFactory() {
		cache = new HashMap<String, EntityManagerFactory>();
	}
	@Override
	public void setService(String serviceId, Object service) {
		IServiceProvider site=(IServiceProvider)service;
		IChip c = (IChip) site.getService(IChip.class.getName());
		this.chipId = c.info().getId();
		this.sp=site;
	}
	/**
	 * 获取实体工厂
	 * @param persistenceUnitName persistence.xml中配置的单元名
	 * @param props jpa的属性。
	 */
	@Override
	public EntityManagerFactory getFactory(String persistenceUnitName,
			Map<String, Object> props) {
		if (cache.containsKey(persistenceUnitName)) {
			return cache.get(persistenceUnitName);
		}
		Thread thread = Thread.currentThread();
		ClassLoader old = thread.getContextClassLoader();
		ClassLoader cl = this.getClass().getClassLoader();
		if (!old.equals(cl)) {
			thread.setContextClassLoader(cl);
		}
		Map<String, Object> map = props == null ? new HashMap<String, Object>()
				: props;
		map.put("eclipselink.classloader", cl);

		EntityManagerFactory factory = null;
		factory = createEntityManagerFactory(persistenceUnitName, props);
		cache.put(persistenceUnitName, factory);
		return factory;
	}

	private EntityManagerFactory createEntityManagerFactory(
			String persistenceUnitName, Map<String, Object> props) {
		if(sp==null){
			throw new EcmException("数据源服务提供器未注入，检查持久模块服务是否声明为开放服务");
		}
		EntityManagerFactory emf = null;
		try {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName,
					props);
			EntityManager em = emf.createEntityManager();

			Map<String, Object> properties = emf.getProperties();
			String jdbcUrl = (String) properties
					.get("javax.persistence.jdbc.url");
			String jdbcDriver = (String) properties
					.get("javax.persistence.jdbc.driver");
			String jdbcUser = (String) properties
					.get("javax.persistence.jdbc.user");
			String jdbcPass = (String) properties
					.get("javax.persistence.jdbc.password");
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			sb.append(String.format("\"requestPluginId\":\"%s\",", chipId));
			sb.append(String.format("\"jdbcUrl\":\"%s\",", jdbcUrl));
			sb.append(String.format("\"jdbcDriver\":\"%s\",", jdbcDriver));
			sb.append(String.format("\"jdbcUser\":\"%s\",", jdbcUser));
			sb.append(String.format("\"jdbcPassword\":\"%s\"", jdbcPass));
			sb.append("}");
			String json = sb.toString();
			Object ds = sp.getService(json);
			if (ds == null) {
				throw new EcmException("sos的database中分配数据源失败，数据源为空，请求：" + json);
			}
			em.close();
			emf.close();
			Map<String,Object> map=new HashMap<String, Object>();
			map.putAll(properties);
			map.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory(persistenceUnitName, /*additionalProperties*/
					map);
			return emf;
		} catch (Exception e) {
			CJSystem.current().environment().logging().error(getClass(),e);
			throw new EcmException("Failed to create entity manager factory: "
					+ e.getMessage(), e);
		}

	}
	/**
	 * 获取实体工厂
	 * @param persistenceUnitName persistence.xml中配置的单元名
	 */
	public final EntityManagerFactory getFactory(String persistenceUnitName) {
		return getFactory(persistenceUnitName, null);
	}

}
