package cj.lns.chip.sos.website.portal.basic.component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISnsReceptionSelector;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.plugins.moduleable.IModuleContext;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonElement;

@CjService(name = "/")
public class PortalCanvasRenderComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// 作用适配客户端类型，并读取场景，为终端插件设画布
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sc = IServicewsContext.context(frame);
		IRemoteDevice device = sc.device(frame);
		IPin down = null;
		String deviceType = "";
		IModuleContext ctx = m.context();
		String sceneid = frame.parameter("useSceneId");
		String theme = frame.parameter("useTheme");
		// String canvas = frame.parameter("useCanvas");//与场景同名，因此不必用
		// String swsid = frame.parameter("swsId");

		String canvasPath = "";
		String scenePath = "";

//		if (device.isBrowser()) {// browser插件
			down = m.downriver("browser");
			deviceType = "browser";
			canvasPath = String.format("/%s.browser.canvas", sceneid);
			scenePath = String.format("/%s.browser.scene", sceneid);
//		} else {
//			down = m.downriver("mobile");
//			deviceType = "mobile";
//			canvasPath = String.format("/%s.mobile.canvas", sceneid);
//			scenePath = String.format("/%s.mobile.scene", sceneid);
//		}
		Document doc = ctx.html(canvasPath, m.site().contextPath(), "utf-8");
		setTheme(doc, m.site().contextPath(), deviceType, theme);

		ISnsReceptionSelector selector = (ISnsReceptionSelector) m.site()
				.getService(ISnsReceptionSelector.KEY);
		String snsdeviceUrl = selector
				.selectDeviceUrl(sc.owner().hashCode());
		String devicetoken="xxxx";
		String devicectr = String.format(
				"<input type='hidden' id='device' ws='%s' uid='%s' swsid='%s' value='%s'>",
				snsdeviceUrl,sc.visitor().principal(),sc.swsid(),devicetoken);
		doc.body().append(devicectr);

		String snsfsUrl = selector.selectFsUrl(sc.owner().hashCode());
		String fstoken="xxxx";
		String fsctr = String.format(
				"<input type='hidden' id='fs' ws='%s' uid='%s' swsid='%s' value='%s'>",
				snsfsUrl,sc.visitor().principal(),sc.swsid(),fstoken);
		doc.body().append(fsctr);

		byte[] b = ctx.resource(scenePath);
		String sceneJson = new String(b);
		circuit.attribute("$.terminus.canvas", doc);
		circuit.attribute("$.terminus.scene", new Gson()
				.fromJson(sceneJson, JsonElement.class).getAsJsonObject());
		down.flow(frame, circuit);
	}

	void setTheme(Document doc, String prefix, String deviceType,
			String theme) {
		if (!prefix.endsWith("/")) {
			prefix = String.format("%s/", prefix);
		}
		Elements es = doc.select("link");
		for (Element e : es) {
			String href = e.attr("href");
			String reg = String.format(".%sthemes/%s/(\\w+)/.*", prefix,
					deviceType);
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(href);
			if (m.find()) {
				String themeInDoc = m.group(1);
				if (theme.equals(themeInDoc)) {
					continue;
				}
				String t = href.replaceFirst(
						String.format("%s/%s", deviceType, themeInDoc),
						String.format("%s/%s", deviceType, theme));
				// String t = m.appendReplacement(sb, themeName);
				e.attr("href", t);
			}
		}
	}
}
