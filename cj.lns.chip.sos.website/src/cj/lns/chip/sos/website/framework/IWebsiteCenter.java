package cj.lns.chip.sos.website.framework;

import cj.studio.ecm.annotation.CjExotericalType;

/**
 * 站点中心
 * <pre>
 * 
 * 用于获取本地或远程的网站，可以是服务操作系统也可以是普通的网站
 * 此功能借助于lns运计算平台的远程获取功能实现，无论本地或远程，均视为远程请求
 * 因此服务操作系统自身不必实现切换功能。
 * 客户端脚本以ajax打开，并为之加上profile快捷板
 * 
 * 站点中心访问网站时，为网站加上profile快捷板或其它装饰
 * 
 * 普通的网站在虚拟终点站上，所以每个操作系统在云计算的物理层面，均拥有一个虚拟终点站，该站用于虚拟安装web，并由station抓取。
 * </pre>
 * @author carocean
 *
 */
@CjExotericalType
public interface IWebsiteCenter {
	
}
