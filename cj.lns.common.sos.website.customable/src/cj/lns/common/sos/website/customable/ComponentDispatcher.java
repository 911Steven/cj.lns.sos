package cj.lns.common.sos.website.customable;

import java.util.Map;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.net.nio.NetConstans;
import cj.studio.ecm.net.web.HttpCircuit;
import cj.studio.ecm.net.web.page.context.JssPageContext;
import cj.studio.ecm.script.IJssModule;
import cj.ultimate.util.FileHelper;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 模块内组件资源调度，按协议地址调度
 * 
 * <pre>
 * －组件的地址
 * －无论从哪个通道查询页面资源，页面资源均是共享的。
 * </pre>
 * 
 * @author carocean
 *
 */
class ComponentDispatcher implements ISink {
	Map<String, IComponent> components;
	
	public ComponentDispatcher(Map<String, IComponent> components) {
		this.components = components;
	}

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		String href = frame.path();
		if (!"/".equals(frame.path()) && href.endsWith("/")) {
			href = href.substring(0, href.length() - 1);
		}
		if (!components.containsKey(href)) {
			if (!searchJss(frame, circuit, plug)) {
				throw new CircuitException("404", String.format(
						"请求的组件%s在模块%s中不存在", href,
						ServiceosWebsiteModule.get().chip().info().getId()));
			}
			return;
		}
		IComponent p = components.get(href);
		p.flow(frame, circuit, plug);
	}

	private boolean searchJss(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		String rpath = frame.path();
		if (rpath.length() > 1 && rpath.endsWith("/")) {
			rpath = rpath.substring(0, rpath.length() - 1);
		}
		String filePath = FileHelper.getFilePathNoEx(rpath);
		filePath = filePath.replace("/", ".");
		String jssSelectName = "";
		if (filePath.startsWith(".")) {
			jssSelectName = String.format("$.cj.jss.sos%s",
					 filePath);
		} else {
			jssSelectName = String.format("$.cj.jss.sos.%s",
					filePath);
		}
		Object jss = null;
		IServiceosWebsiteModule mod=ServiceosWebsiteModule.get();
		jss = mod.site().getService(jssSelectName);
		if (jss == null) {
			return false;
		}
		ScriptObjectMirror m = (ScriptObjectMirror) jss;
		if (!m.hasMember("flow")) {
			throw new CircuitException(NetConstans.STATUS_503, String
					.format("在jssComponent页面%s中没有发现flow方法。", jssSelectName));
		}
		JssPageContext ctx = new JssPageContext(null,
				((HttpCircuit) circuit).httpSiteRoot());
		try {
			m.callMember("flow", frame, circuit, plug, ctx);
		} catch (Exception e) {
			CJSystem.current().environment().logging().error(String
					.format("jssComponent页面脚本错误:%s,信息：%s", jssSelectName, e));
			throw new CircuitException(NetConstans.STATUS_503, e);
		}
		return true;
	}

}
