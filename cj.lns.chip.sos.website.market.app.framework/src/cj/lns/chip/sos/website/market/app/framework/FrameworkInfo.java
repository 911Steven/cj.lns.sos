package cj.lns.chip.sos.website.market.app.framework;

import cj.lns.chip.sos.website.framework.IFrameworkInfoReporter;
import cj.lns.chip.sos.website.framework.info.PluginInfo;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.ultimate.gson2.com.google.gson.Gson;
@CjService(name="/frameworkInfo",isExoteric=true)
public class FrameworkInfo {
//	@Override
//	protected void doAjax(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//			doRender(user, frame, ctx);
//	}
//	@Override
//	protected void doRender(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//		if("json".equals(frame.parameter("type"))){
//			render(ctx.circuit());
//			return;
//		}
//		if(frame.containsParameter("appIcon")){
//			String id=frame.parameter("appIcon");
//			renderIcon(ctx.circuit(),id);
//			return;
//		}
//		Element panel = ctx.panel().e();
//		Document doc = ctx.html("/frameworkInfo.html",
//				String.format("/%s", chipInfo().getId()), "utf-8");
//		Elements es = doc.body().children();
//		if (es.isEmpty())
//			return;
//		Element e = es.first();
//		
//		panel.appendChild(e);
//		ctx.panel().head().add(doc.head());
//	}
	private void renderIcon(Circuit circuit, String id) {
		IServiceProvider site=ServiceosWebsiteModule.get().site();
		IFrameworkInfoReporter fr=(IFrameworkInfoReporter)site.getService("frameworkInfoReporter");
		cj.lns.chip.sos.website.framework.info.FrameworkInfo info=fr.get();
		for(PluginInfo pi:info.getPlugins()){
			if(pi.getAssemblyGuid().equals(id)){
				byte[] icon=pi.getAssemblyIconBytes();
				circuit.content().writeBytes(icon);
				break;
			}
		}
		
	}
	private void render(Circuit circuit) {
		IServiceProvider site=ServiceosWebsiteModule.get().site();
		IFrameworkInfoReporter fr=(IFrameworkInfoReporter)site.getService("frameworkInfoReporter");
		cj.lns.chip.sos.website.framework.info.FrameworkInfo info=fr.get();
		String json=new Gson().toJson(info);
		circuit.content().writeBytes(json.getBytes());
	}
}
