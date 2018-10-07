package cj.lns.chip.sos.service.db;

import javax.sql.DataSource;

public interface IDataSourceAdapter {
	DataSource get(String requestPluginId,String jdbcUrl, String jdbcDriver,
			String jdbcUser, String jdbcPass) ;
}
