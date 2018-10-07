package cj.lns.chip.sos.website.portal.basic.browser.component;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

@CjService(name = "/")
public class BrowserRenderComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		// 组装画面，向各模块发出渲染要求，在此组装，组装就是将插件的内容替换掉画布中场景插件对应的内容。

		Document canvas = (Document) circuit.attribute("$.terminus.canvas");
		circuit.removeAttribute("$.terminus.canvas");
		JsonObject scene = (JsonObject) circuit.attribute("$.terminus.scene");
		circuit.removeAttribute("$.terminus.scene");

//		synchronized (canvas) {
			renderScene(scene, canvas, frame, circuit);
//		}

	}

	private void renderScene(JsonObject scene, Document canvas, Frame frame,
			Circuit circuit) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		// 先渲染非排版区域，最后渲染根区域，版本区域是对非根区域的排版
		String layout = ElementGet.getJsonProp(scene.get("layout"));
		String layoutPlugin = "";
		String layoutTitle = "";
		JsonArray jregions = scene.get("regions").getAsJsonArray();
		Map<String, String> regions = new HashMap<>();
		for (JsonElement je : jregions) {
			JsonObject jo = je.getAsJsonObject();
			String name = ElementGet.getJsonProp(jo.get("name"));
			String plugin = ElementGet.getJsonProp(jo.get("plugin"));
			String title = ElementGet.getJsonProp(jo.get("title"));
			if (regions.containsKey(name)) {
				throw new CircuitException("404",
						String.format("区域重名！区域:%s 终端插件:%s。", plugin,
								m.chip().info().getId()));
			}
			if (layout.equals(name)) {
				layoutPlugin = plugin;
				layoutTitle = title;
				continue;
			}
			regions.put(name, plugin);
		}
		IPin down = m.downriver(layout);
		if (down == null) {
			throw new CircuitException("404",
					String.format("未发现区域:%s中的插件，检查此插件是否在终端插件:%s之下。",
							layoutPlugin, m.chip().info().getId()));
		}
		frame.parameter("region.name", layout);
		frame.parameter("region.title", layoutTitle);
		circuit.attribute("$.terminus.canvas", canvas);
		circuit.attribute("$.terminus.regions", regions);
		down.flow(frame, circuit);// 在layout中排版并输出

	}

}
