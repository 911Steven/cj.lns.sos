package cj.lns.chip.sos.website.security;

import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;

/**
 * 资源实现
 * <pre>
 * ~ 定义：一个资源比如App，它可以有多个实现比如：控制面板、信息中心等，每个实现实际上由一个提供器为之提供安全对象。
 * ~ 安全资源实现用于读取提供器，分析安全对象
 * </pre>
 * @author carocean
 *
 */
public interface ISecurityResourceImpl {
	Class<?> soProviderClass();
	String resourceId();
	String resourceName();
	String valueId();
	String valueName();
	IAclFinder finder();
	IAclSetting setting();
	ISecurityObject root();
	/**
	 * 根是否可在授权界面中可见
	 * <pre>
	 *
	 * </pre>
	 * @param root 根资源
	 * @param subject
	 * @param ctx
	 * @return
	 */
	boolean isRootVisible(ISecurityObject root,ISubject subject, IServicewsContext ctx);
	/**
	 * 参数中的主体和上下文用于授权界面自身的分级再授权。即过滤掉当前主体无权的资源，当前主体还可对可见的资源进行再授权。
	 * <pre>
	 *
	 * </pre>
	 * @param resourceId
	 * @param valueId
	 * @param subject
	 * @param ctx
	 * @return
	 */
	List<ISecurityObject> childs(String resourceId,String valueId,ISubject subject, IServicewsContext ctx);
	ISecurityObject find(String resourceId,String valueId,ISubject subject, IServicewsContext ctx);
	public String getHostId();
}
