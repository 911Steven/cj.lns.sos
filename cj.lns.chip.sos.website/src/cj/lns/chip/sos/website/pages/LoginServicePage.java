package cj.lns.chip.sos.website.pages;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;
@CjService(name="/pages/loginService")
public class LoginServicePage  extends Page{
	@Override
	public void doPage(Frame frame,Circuit circuit, IPlug plug, PageContext ctx)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		/*
		 * > 先到认证模块中认证
		 * > 通过认证后由客户端收到成功信息，客户端发起到视窗渲染请求，这归于登录与重定向到渲染已在同一会话的机制
		 */
		IPin auth = m.downriver("auth");
		String authUrl="/auth";
		if (frame.containsQueryString()) {
			authUrl = String.format("%s?%s", authUrl, frame.queryString());
		}
		frame.head("root-name",frame.rootName());
		frame.url(authUrl);
		auth.flow(frame, circuit);
	}
}
