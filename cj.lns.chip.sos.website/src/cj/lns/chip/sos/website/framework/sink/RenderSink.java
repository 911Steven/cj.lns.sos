package cj.lns.chip.sos.website.framework.sink;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;

public class RenderSink implements ISink {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IPin sws = plug.branch("servicews");
		
		// 先flow视窗，在视窗模块中为会话设置上下文后返回，如果状态是成功，则flow portal
		sws.flow(frame, circuit);
		//注释掉的原因：应由视窗来跳转
//		int state = Integer.valueOf(circuit.status());
		// 为框架渲染请求,需要在视窗中验证：是持有者登录？还是来访者登录？如果是来访者登录，则会话中已包含持有者，这些判断逻辑均在视窗模块中验证
		// IPin portal = plug.branch("portal");
		// if (state >= 200 && state < 300) {
		// portal.flow(frame, circuit);
		// }else{
		// throw new CircuitException(circuit.status(), circuit.message());
		// }
	}

}
