package cj.lns.chip.sos.service.db;

import cj.studio.ecm.annotation.CjService;

@CjService(name="configableDataSourceStragety")
public class ConfigableDataSourceStragety implements IAccessDataSourceStrategy {

	@Override
	public String useProvider(String requestPluginId, String jdbcUrl,
			String jdbcDriver, String jdbcUser,String jdbcpassword) {
		return "c3p0DataSourceProvider";
	}

}
