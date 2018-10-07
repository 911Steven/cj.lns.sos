package cj.lns.common.sos.website.customable;

import cj.studio.ecm.graph.ISink;

/**
 * 组件过滤器
 * 
 * <pre>
 * －必须声明为服务
 * </pre>
 * 
 * @author carocean
 *
 */
public interface IComponentFilter extends ISink {
	/**
	 * 有两个回路，内核直接过来的，上游模块下行的
	 * 
	 * <pre>
	 * ·0表示两个回路均拦截
	 * ·1表示只拦截内核的
	 * ·－1表示只拦截上游过来的
	 * 其它值均表示两个回路均拦截
	 * </pre>
	 * 
	 * @return
	 */
	int matchCircuit();

	/**
	 * 顺序
	 * 
	 * <pre>
	 *
	 * </pre>
	 * 
	 * @return
	 */
	int sort();

}
