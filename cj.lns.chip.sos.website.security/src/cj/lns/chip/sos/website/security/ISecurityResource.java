package cj.lns.chip.sos.website.security;

/**
 * 用于给授权界面调用
 * 
 * <pre>
 * ~ 表示一个资源，下拥有多个安全对象提供器，如栏目资源类Portlet定义的对应的就是一个资源，而这种栏目可由多个提供器提供
 * ~ 资源到各个提供器中获取安全对象，也可通过位置获取安全对象
 * </pre>
 * 
 * @author carocean
 *
 */
public interface ISecurityResource {
	/**
	 * 资源实现的标识是提供器ROOT对象的valueId
	 * <pre>
	 *
	 * </pre>
	 * @return
	 */
	String[] enumResourceImpl();
	/**
	 * <pre>
	 *
	 * </pre>
	 * @return
	 */
	ISecurityResourceImpl resourceImpl(String valueId);
	/**
	 * 资源实现的标识是提供器ROOT对象的valueId
	 * <pre>
	 *
	 * </pre>
	 * @return
	 */
	boolean containsResourceImpl(String valueId);
	void add(String valueId, ISecurityResourceImpl impl);
}
