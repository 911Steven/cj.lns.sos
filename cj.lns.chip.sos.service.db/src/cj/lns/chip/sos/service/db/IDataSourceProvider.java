package cj.lns.chip.sos.service.db;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

public interface IDataSourceProvider {
	DataSource get(String jdbcUrl, String jdbcDriver, String jdbcUser,
			String jdbcPass) throws PropertyVetoException ;
}
