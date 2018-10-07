package cj.lns.chip.sos.service.db;
/**
 * 访问数据源的策略
 * <pre>
 * －策略由db中心统一控制，各插件的使用者只需关心连哪个库，它的用户名和密码是什么即可。db中心以jdbcUrl为对库的索引键
 * －db中心默认实现了基于assembly.properties文件中配置访问库策略的模式。
 * </pre>
 * @author carocean
 *
 */
public interface IAccessDataSourceStrategy {

	String useProvider(String requestPluginId,String jdbcUrl, String jdbcDriver, String jdbcUser,String jdbcPassword);

}
