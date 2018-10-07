package cj.lns.chip.sos.service.db;

import java.util.Properties;

import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.annotation.CjService;

public abstract class DataSourceProvider implements IDataSourceProvider,
		IServiceAfter {
	protected Properties poolProps;

	@Override
	public void onAfter(IServiceSite site) {
		poolProps = new Properties();
		String[] set = site.enumProperty();
		CjService def = this.getClass().getAnnotation(CjService.class);
		String name = String.format("%s.", def.name());
		for (String k : set) {
			if (k.startsWith(name)) {
				poolProps.put(k, site.getProperty(k));
			}
		}
	}
}
