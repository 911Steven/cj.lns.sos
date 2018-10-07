package cj.lns.chip.sos.website.framework.sink;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.ultimate.util.StringUtil;

/**
 * partsink专门处理外部来的http协议，因此独立一个sink类型
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
public class PartSink implements ISink {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String contentPath = frame.rootName();
		if (StringUtil.isEmpty(contentPath)) {// 为框架渲染请求,需要在视窗中验证：是持有者登录？还是来访者登录？如果是来访者登录，则会话中已包含持有者
			IPin sws = plug.branch("servicews");
			sws.flow(frame, circuit);
			//注释掉的原因：因为这属于视窗逻辑，它认证成功后爱去哪去哪
//			int state = Integer.valueOf(circuit.status());
//			if (state >= 200 && state < 300) {
//				IPin portal = plug.branch("portal");
//				portal.flow(frame, circuit);
//			}else{
//				throw new CircuitException(circuit.status(), circuit.message());
//			}
			return;
		}
		if (!plug.containsBranch(contentPath)) {
			throw new CircuitException("404",
					String.format("模块不存在：%s", contentPath));
		}
		IPin p = plug.branch(contentPath);
//		String path = frame.path();
//		String moduleRelateUrl = path.substring(contentPath.length()+1, path.length());
		frame.url(frame.relativeUrl());
		p.flow(frame, circuit);
	}

}
