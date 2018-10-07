package cj.lns.chip.sos.website.portal.basic.mobile.component;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/")
public class MobileRenderComponent implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		// 组装画面，向各模块发出渲染要求，在此组装，组装就是将插件的内容替换掉画布中场景插件对应的内容。

		Document canvas = (Document) circuit.attribute("$.terminus.canvas");
		circuit.removeAttribute("$.terminus.canvas");
		// JsonObject scene = (JsonObject)
		// circuit.attribute("$.terminus.scene");
		circuit.removeAttribute("$.terminus.scene");
		
		IRemoteDevice device = IRemoteDevice.remoteDevice(frame);
		canvas.body()
				.append(String.format("<p>browserType:%s</p>", device.getBrowserType()));
		canvas.body().append(
				String.format("<p>browserVersion:%s</p>", device.getBrowserVersion()));
		canvas.body().append(
				String.format("<p>platformSeries:%s</p>", device.getPlatformSeries()));
		canvas.body().append(
				String.format("<p>platformType:%s</p>", device.getPlatformType()));
		canvas.body().append(
				String.format("<p>platformVersion:%s</p>", device.getPlatformVersion()));
		circuit.content().writeBytes(canvas.html().getBytes());
	}

}
