package cj.lns.chip.sos.website.sws.component;

import cj.lns.chip.sos.service.sws.ServicewsBody;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

@CjService(name = "/getServicewsBody.service")
public class GetServicewsBody implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		String swsid = frame.parameter("swsid");
		writeServicews(swsid, circuit);
	}

	protected void writeServicews(String swsid, Circuit circuit2)
			throws CircuitException {
		// 认证成功则查找默认视窗，如果有则
		Frame frame = new Frame("getServicewsBody /sws/instance sos/1.0");
		frame.parameter("swsid", swsid);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException("503",
						String.format("获取视窗失败：%s", swsid));
			}
			String json = new String(back.content().readFully());
			ServicewsBody body = new Gson().fromJson(json, ServicewsBody.class);
			if (StringUtil.isEmpty(body.getFaceImg())) {
				body.setFaceImg("../cjdk/module-icon.svg");
			}else{
				String src=String.format("../resource/ud/%s?path=%s://system/faces", body.getFaceImg(),swsid);
				body.setFaceImg(src);
			}
			if(body.getExtra().isEmpty()){
				body.getExtra().put("intro", body.getSwsDesc());
			}
			circuit2.content().writeBytes(new Gson().toJson(body).getBytes());
			return;

		}
		circuit2.content().writeBytes("{}".getBytes());
	}
}
