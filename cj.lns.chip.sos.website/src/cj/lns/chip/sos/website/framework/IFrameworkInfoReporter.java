package cj.lns.chip.sos.website.framework;

import cj.lns.chip.sos.website.framework.info.FrameworkInfo;

/**
 * 杠架信息报告器
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
public interface IFrameworkInfoReporter {
	String KEY_SERVICE_NAME="frameworkInfoReporter";
	FrameworkInfo get();
}
