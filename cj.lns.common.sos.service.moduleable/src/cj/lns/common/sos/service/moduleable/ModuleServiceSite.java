package cj.lns.common.sos.service.moduleable;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import cj.lns.chip.sos.service.framework.IServiceosModuleServiceProvider;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceInvertInjection;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.annotation.CjServiceSite;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.plugins.moduleable.IModuleServiceSite;

@CjService(name = "moduleServiceSite")
public class ModuleServiceSite
		implements IModuleServiceSite, IServiceosModuleServiceProvider {
	@CjServiceSite
	private IServiceSite site;
	private IServiceProvider sosprovider;
	@CjServiceInvertInjection
	@CjServiceRef(refByName = "persistenceFactory")
	private PersistenceFactory factory;
	IPin out;
	IPin in;
	@Override
	public Object databaseCloud() {
		return site.getService("databaseCloud");
	}
	@Override
	public IPin in() {
		return in;
	}

	@Override
	public IPin out() {
		return out;
	}
	@Override
	public EntityManagerFactory factory(String persistenceUnitName) {
		return factory.getFactory(persistenceUnitName);
	}
	@Override
	public EntityManagerFactory factory(String persistenceUnitName,
			Map<String, Object> props) {
		return factory.getFactory(persistenceUnitName, props);
	}

	@Override
	public <T> ServiceCollection<T> getServices(Class<T> serviceClazz) {
		ServiceCollection<T> col = site.getServices(serviceClazz);
		if (sosprovider != null) {
			ServiceCollection<T> col2 = sosprovider.getServices(serviceClazz);
			if(col2!=null&&!col2.isEmpty())
			col = (ServiceCollection<T>) col.combine(col2);
		}
		return col;
	}

	@Override
	public Object getService(String serviceId) {
		Object service = site.getService(serviceId);
		if (service == null && sosprovider != null) {
			service = sosprovider.getService(serviceId);
		}
		return service;
	}

	@Override
	public void setFrameworkServiceProvider(IServiceProvider provider) {
		this.sosprovider = provider;
	}

}
