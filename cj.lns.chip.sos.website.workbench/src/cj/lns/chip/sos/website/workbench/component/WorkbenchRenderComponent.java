package cj.lns.chip.sos.website.workbench.component;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.util.StringUtil;

@CjService(name = "/")
public class WorkbenchRenderComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit,IPlug plug) throws CircuitException {
		// 组装画面，向各模块发出渲染要求，在此组装，组装就是将插件的内容替换掉画布中场景插件对应的内容。


		@SuppressWarnings({ "unchecked" })
		Map<String, String> regions = (Map<String, String>) circuit
				.attribute("$.terminus.regions");
		Document canvas = (Document) circuit.attribute("$.terminus.canvas");
		circuit.removeAttribute("$.terminus.canvas");
		circuit.removeAttribute("$.terminus.regions");

		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();

		IFlowContent fctx = frame.content();
		fctx.clear();
		IFlowContent ctx = circuit.content();
		ctx.clear();
		for (String name : regions.keySet()) {
			String plugin = regions.get(name);
			frame.parameter("region.name", name);
			frame.removeParameter("region.title");
			frame.parameter("region.plugin", plugin);
			Elements region = canvas.select(String.format("[region=%s]", name));
			if (region.size() < 1) {
				continue;
			}
			IPin down = m.downriver(plugin);
			if (down == null) {
				canvas.select(String.format("[region=%s]", name)).remove();
				CJSystem.current().environment().logging()
						.warn(String.format(
								"404 未发现区域:%s中的插件，检查此插件是否在终端插件:%s之下。", name,
								m.chip().info().getId()));
				continue;
			}
			for (Element position : region) {// 一个区域可能被放在多处，当一个区域被放到多处时，以usage标明用途。如栏目，则在区域元素上加上usage='left',或usage='right'属性
				fctx.writeBytes(position.html().getBytes());

				String usage = position.hasAttr("usage") ? position.attr("usage")
						: "";
				frame.parameter("region.usage", usage);

				position.empty();

				down.flow(frame, circuit);

				fctx.clear();
				if (ctx.readableBytes() < 1) {
					if("portlet".equals(position.attr("region"))){
						position.attr("used","empty");
					}
					continue;
				}
				Document part = Jsoup.parse(new String(ctx.readFully()));

				ctx.clear();
				
				canvas.head().append(part.head().html());
				//注释掉的原因：childNodes在添加时必须clone，如不考的话则会修改集合导致并发错误。而如果用元素方式，则会漏掉非标签的文本内容
//				List<Node> heads = part.head().childNodes();
//				for (int i = 0; i < heads.size(); i++){
//					Node e=heads.get(i);
//					canvas.head().appendChild(e);
//				}
				position.append(part.body().html());
//				List<Node> childrens = part.body().childNodes();
//				int len=childrens.size();
//				for (int i = 0; i < len; i++) {
//					Node e = childrens.get(i);
//					position.appendChild(e.clone());
//				}
			}
		}
		//默认应用
		String defaultApp=frame.parameter("defaultAppId");
		if(!StringUtil.isEmpty(defaultApp)){
			canvas.select(".desktop[region=desktop]").attr("defaultAppId",defaultApp);
		}
		circuit.content().writeBytes(canvas.toString().getBytes());
	}

}
