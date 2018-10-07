package cj.lns.common.sos.website.customable;

import cj.studio.ecm.graph.ISink;
/**
 * 模块内的组件
 * <pre>
 * －必须声为名服务，服务名格式：协议://路径
 * －组件的地址一定是带协议的，如：http://path/myComponent.html,security://path/my.html
 * －如果任意协议地址，则不能用组件，而是在Program的内部pin管道中添加Sink以拦截
 * </pre>
 * @author carocean
 *
 */
public interface IComponent extends ISink {
}
