package cj.lns.chip.sos.website.framework;

import cj.studio.ecm.IChipInfo;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
/**
 * 表示一个sos的信息港
 * <pre>
 * 信息港符合信息处理范式，信息入港，经由一系列组件处理；
 * 	组件处理完，可将产生的信息出港，进而使得与其它sos或终端交互
 * 作用：
 * 1.接受或处理入港信息
 * 2.接受或处理出港信息。
 * 3.主动推送出港信息。（默认支持websocket［当信息港在sos内时，也支持rio-udt方式［当信息港独立部署时）
 * 
 * </pre>
 * @author carocean
 *
 */
public interface ICyberport {
	void setParent(IServiceProvider parent);
	/**
	 * 接受入港信息
	 * <pre>
	 *
	 * </pre>
	 * @param canvas
	 * @param frame
	 * @param circuit
	 */
	void input(IServicewsContext canvas, Frame frame, Circuit circuit)throws CircuitException;
	/**
	 * 接受出港信息。
	 * <pre>
	 *
	 * </pre>
	 * @param canvas
	 * @param ci
	 * @param frame
	 * @param c
	 */
	void output(IServicewsContext canvas, IChipInfo ci, Frame frame, Circuit c)throws CircuitException;
	/**
	 * 用于出港信息向外部net推送
	 * <pre>
	 *
	 * </pre>
	 * @param pusher
	 */
	void setPusher(IPin pusher);
}
