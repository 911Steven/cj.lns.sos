package cj.lns.chip.sos.website.security;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;

/**
 * 安全中心
 * 
 * <pre>
 * · 向所有模块收集资源提供器。
 * · 资源索引
 * </pre>
 * 
 * @author carocean
 *
 */
public interface ISecurityCenter {
	void init();
	void scans(IServiceosWebsiteModule m);
	String[] enumCategory();
	Category category(String categoryid);
	String[] enumSecurityResource();
	ISecurityResource resource(String resourceId);
	boolean containsResource(String resourceId);
}
