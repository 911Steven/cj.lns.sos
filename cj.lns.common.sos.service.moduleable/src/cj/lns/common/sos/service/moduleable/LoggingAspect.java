package cj.lns.common.sos.service.moduleable;

import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.bridge.IAspect;
import cj.studio.ecm.bridge.ICutpoint;
import cj.studio.ecm.logging.ILogging;

/**
 * 日志方面
 * 
 * <pre>
 * 方面名：logging
 * －用于为服务的方法添加日志
 * －用 @cjBridge声明方面，或用xml,json方式
 * 
 * 注意：日志方面仅支持以下注解的方法：CjRemoteMethod，CjTransaction，CjLogging
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "logging")
public class LoggingAspect implements IAspect {

	@Override
	public Object cut(Object bridge, Object[] args, ICutpoint point) {
		CjRemoteMethod p = point.getMethodAnnotation(CjRemoteMethod.class);
		CjTransaction t = point.getMethodAnnotation(CjTransaction.class);
		CjLogging l = point.getMethodAnnotation(CjLogging.class);
		if (p == null&&t==null&&l==null) {
			return point.cut(bridge, args);
		}
		try {
			return point.cut(bridge, args);
		} catch (Exception e) {
			ILogging log = CJSystem.current().environment().logging();
			String err = String.format("\033[0;31;1m日志方面信息：服务方法 %s %s 信息：%s\033[0m",
					point.getServiceDefId(), point.getMethodName(),
					 e.getMessage());
			log.error(point.getType(), err);
			throw e;
		}
	}

	@Override
	public Class<?>[] getCutInterfaces() {
		return null;
	}

}
