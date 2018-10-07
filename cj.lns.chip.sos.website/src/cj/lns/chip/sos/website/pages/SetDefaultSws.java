package cj.lns.chip.sos.website.pages;

import java.util.Map;

import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.WebUtil;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;

@CjService(name = "/public/setDefaultSws.html")
public class SetDefaultSws extends Page {

	@Override
	protected void doPage(Frame frame,Circuit circuit, IPlug plug, PageContext ctx)
			throws CircuitException {
		if(frame.content().readableBytes()>0){
			String params=new String(frame.content().readFully());
			Map<String, Object> map=WebUtil.parserParam(params);
			String owner=(String)map.get("owner");
			String swsId=(String)map.get("swsid");
			setDefaultSws(owner, swsId);
		}
		
	}

	protected void setDefaultSws(String owner, String swsId)
			throws CircuitException {
		// 认证成功则查找默认视窗，如果有则
		Frame frame = new Frame("setDefaultSws /sws/owner sos/1.0");
		frame.parameter("userCode", owner);
		frame.parameter("swsid", swsId);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException("503",
						String.format("设置用户%s的默认视窗失败", owner));
			}

		}
	}
}
