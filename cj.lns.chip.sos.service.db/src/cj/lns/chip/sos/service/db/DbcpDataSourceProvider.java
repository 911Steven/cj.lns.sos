package cj.lns.chip.sos.service.db;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

public class DbcpDataSourceProvider extends DataSourceProvider implements IDataSourceProvider{
	
	@Override
	public DataSource get(String jdbcUrl, String jdbcDriver, String jdbcUser,
			String jdbcPass) throws PropertyVetoException {
		// TODO Auto-generated method stub
		return null;
	}

}
