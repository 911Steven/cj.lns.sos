package cj.lns.chip.sos.website.market.portlet;

import java.io.IOException;
import java.io.InputStream;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.util.SvgThemeTool;
import cj.lns.common.sos.website.customable.IComponentFilter;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.plugins.moduleable.IModuleContext;
import cj.ultimate.util.StringUtil;

@CjService(name = "moduleResourceFilter")
public class ModuleResourceFilter extends SvgThemeTool implements IComponentFilter {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// TODO Auto-generated method stub
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IModuleContext ctx = m.context();
		String root = frame.rootName();
		if (StringUtil.isEmpty(root)) {//如果是渲染则用于写本插件资源，其它的资源交给应用。
			String path = frame.path();
			if (ctx.isWebResource(path, ".html|.htm|.service")) {
				if (path.endsWith(".svg")) {
					IServicewsContext sws = IServicewsContext.context(frame);
					InputStream in = ctx.resourceAsStream(path);
					try{
					renderSvgByTheme(in, sws, circuit);
					} catch (IOException e) {
						throw new CircuitException("404", e);
					}finally{
						if(in!=null){
							try {
								in.close();
							} catch (IOException e) {
							}
						}
					}
					return;
				}
				byte[] b = ctx.resource(path);
				circuit.content().writeBytes(b);
				return;
			}
			throw new CircuitException("503", "请求栏目资源缺上下文："+frame.url());
		}
		plug.flow(frame, circuit);
	}

	@Override
	public int matchCircuit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
