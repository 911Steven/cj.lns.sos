package cj.lns.chip.sos.website.portal.component;

import java.util.Map;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.portal.ISwsPortalService;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/")
public class PortalRenderComponent implements IComponent {
	@CjServiceRef(refByName = "swsPortalService")
	ISwsPortalService swsPortal;

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		// 取得视窗框架配置插件
		String swsid = frame.parameter("swsid");
		Map<String, String> map = null;
		map = swsPortal.getConfig(swsid);
		IServicewsContext ctx = IServicewsContext.context(frame);
		for (String key : map.keySet()) {
			String value = map.get(key);
			frame.parameter(key, value);
			if("portalId".equals(key)){//只放入框架标识，尽量少占内存。
				ctx.prop("portal.id",value);
			}
			if("platform".equals(key)){//只放入框架标识，尽量少占内存。
				ctx.prop("portal.platform",value);
			}
			if("useSceneId".equals(key)){//只放入框架标识，尽量少占内存。
				ctx.prop("portal.scene",value);
			}
			if("useTheme".equals(key)){//只放入框架标识，尽量少占内存。
				ctx.prop("portal.theme",value);
			}
			if("background".equals(key)){//只放入框架标识，尽量少占内存。
				ctx.prop("portal.background",value);
			}
		}
		IPin cjdk = m.downriver(map.get("portalId"));
		cjdk.flow(frame, circuit);
	}

}
