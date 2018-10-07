package cj.lns.common.sos.website.customable;

import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.plugins.moduleable.IModuleEntrypoint;

/**
 * 模块程序入口
 * 
 * <pre>
 * 它派生于模块入口点
 * 
 * 用法：
 * －在模块内声明为服务
 * 
 * 作用：
 * －模块内的页面统一视为资源
 * －不论采用直连通道还是上下游通道，均可使用模块内的任意页面统一资源
 * －该类仅处理输入流，输出流可直接调用模块输出
 * </pre>
 * 
 * @author carocean
 *
 */
public abstract class Program implements IModuleEntrypoint {

	@Override
	public final void entrypoint(IModule m) {
		ModuleCustomableGraph g=new ModuleCustomableGraph(m);
		g.setChipSite(m.chip().site());
		g.initGraph();
		program(m);
	}
	/**
	 * 可覆盖
	 * <pre>
	 *
	 * </pre>
	 * @param m
	 */
	protected abstract void program(IModule m);

	
}
