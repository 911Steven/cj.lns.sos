package cj.lns.chip.sos.website.sws.sns;

import java.util.Map;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
/**
 * 必须声明为服务，且服务名必须是应用代码：appCode
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
public interface IAppSessionOpener {

	void flow(Map<String, Object> session, Map<String, String> app, Frame frame,
			Circuit circuit)throws CircuitException;

}
