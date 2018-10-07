package cj.lns.chip.sos.website.framework.sink;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.ultimate.util.StringUtil;
//直接sink用于内部模块互通，也用于内部组件向外请求，但外部请求不能使用不到直接sink
public class DirectSink implements ISink {
//	String remoteProcessId;
	public DirectSink() {
//		this.remoteProcessId=remoteProcessId;
	}
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// 两个外部端子： $.remoteOut,$.fetchOut
		// 除了两个外部协议外，对内部模块的调度严格按模块名作为地址调度
		IPin p = null;
		if ("sos/1.0".equals(frame.protocol().toLowerCase())) {
			p = plug.branch("$.remoteOut");
			
//			frame.url(String.format("/%s%s",remoteProcessId, frame.url()));
			
			p.flow(frame, circuit);
			return;
		}
		if ("peer/1.0".equals(frame.protocol().toLowerCase())) {
			p = plug.branch("$.snsOut");
			p.flow(frame, circuit);
			return;
		}
		if ("im/1.0".equals(frame.protocol().toLowerCase())) {
			p = plug.branch("$.snsOut");
			p.flow(frame, circuit);
			return;
		}
		if ("csc/1.0".equals(frame.protocol().toLowerCase())) {
			p = plug.branch("$.cscOut");
			p.flow(frame, circuit);
			if("700".equals(circuit.status())){
				throw new CircuitException(circuit.status(), circuit.message());
			}
			return;
		}
		if ("fetch/1.0".equals(frame.protocol().toLowerCase())) {
			p = plug.branch("portal");
			if (!frame.containsHead("sub-protocol")
					|| StringUtil.isEmpty(frame.head("sub-protocol"))) {
				throw new CircuitException("404",
						"fetch/1.0协议必须指定二级协议：sub-protocol作为请求的目标协议");
			}
			frame.protocol(frame.head("sub-protocol"));
			p.flow(frame, circuit);
			return;
		}
		//注释掉原因：按协议的话因为协议会被转换为大写，则无法与分支中的模块名对应。
//		String t = frame.protocol();
//		String n = t.substring(0, t.indexOf("/"));
//		if (!plug.containsBranch(n)) {
//			throw new CircuitException("404",
//					String.format("协议:%s 对应的模块:%s 不存在。", t, n));
//		}
		//因此改为按地址调度，由于直接sink外部请求无法访问，因此是内部授信的，因此是已认证
		if("/".equals(frame.path())){//根则视为对portal的请求，即等效于/portal
			plug.branch("portal").flow(frame, circuit);;
			return;
		}
		String root=frame.rootName();
		p = plug.branch(root);
		frame.url(frame.relativeUrl());
		p.flow(frame, circuit);
	}

}
