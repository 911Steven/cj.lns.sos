package cj.lns.chip.sos.website.market.app.framework;

import cj.studio.ecm.annotation.CjService;
@CjService(name="/index.html",isExoteric=true)
public class Framework  {
//	@Override
//	protected void doAjax(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//		if(frame.containsParameter("action")){
//			if("sync".equals(frame.parameter("action"))){
//				syncToMarket(user);
//				
//			}
//			return;
//		}
//		if("/".equals(frame.path())){
//			Element panel = ctx.panel().e();
//			Document doc = ctx.html("/framework.html",
//					String.format("/%s", chipInfo().getId()), "utf-8");
//			Elements es = doc.body().children();
//			if (es.isEmpty())
//				return;
//			Element e = es.first();
//			panel.appendChild(e);
//			ctx.panel().head().add(doc.head());
//		}else{
//			ctx.dispatch(this).forward(user, frame);
//		}
//	}
//	private void syncToMarket(ISwsContext user) throws CircuitException {
//		IWorkspace ws=(IWorkspace)ServiceosWebsiteModule.get().site().getService("workspace");
//		if(ws==null){
//			throw new CircuitException("404", "未获取到工作空间");
//		}
//		ws.reportLocalFrameworkInfo(user.current().owner().getCode(), "xxx");
//	}
//	@Override
//	protected void doRender(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//		if(ctx.isResource()){
//			byte[] b=ctx.resource(frame.path());
//			ctx.circuit().content().writeBytes(b);
//			return;
//		}
//		ctx.dispatch(this).forward(user, frame);
//	}
}
