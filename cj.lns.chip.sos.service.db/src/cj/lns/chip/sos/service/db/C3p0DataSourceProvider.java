package cj.lns.chip.sos.service.db;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;

import com.mchange.v2.c3p0.DataSources;

/*http://www.mchange.com/projects/c3p0/#using_combopooleddatasource
 * DataSource ds_unpooled = DataSources.unpooledDataSource("jdbc:postgresql://localhost/testdb", 
                                                        "swaldman", 
                                                        "test-password");
DataSource ds_pooled = DataSources.pooledDataSource( ds_unpooled );

// The DataSource ds_pooled is now a fully configured and usable pooled DataSource.
// The DataSource is using a default pool configuration, and Postgres' JDBC driver
// is presumed to have already been loaded via the jdbc.drivers system property or an
// explicit call to Class.forName("org.postgresql.Driver") elsewhere.
	
 */
@CjService(name = "c3p0DataSourceProvider")
public class C3p0DataSourceProvider extends DataSourceProvider implements
		IDataSourceProvider {
	public DataSource get(String jdbcUrl, String jdbcDriver, String jdbcUser,
			String jdbcPass) throws PropertyVetoException {
		try {
			// ComboPooledDataSource pool = new
			// ComboPooledDataSource();//这种源会报错，说对数据库无访问权限，虽然也能查出数
			// pool.setDriverClass(jdbcDriver);
			// pool.setJdbcUrl(jdbcUrl);
			// pool.setUser(jdbcUser);
			// pool.setPassword(jdbcPass);
			// pool.setProperties(poolProps);
			DataSource un = DataSources.unpooledDataSource(jdbcUrl, jdbcUser,
					jdbcPass);
			DataSource pool = DataSources.pooledDataSource(un, poolProps);
			return pool;
		} catch (Exception e) {
			throw new EcmException(e);
		}
	}
}
