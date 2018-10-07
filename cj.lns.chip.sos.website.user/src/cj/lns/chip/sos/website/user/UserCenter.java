package cj.lns.chip.sos.website.user;

import cj.studio.ecm.annotation.CjService;

@CjService(name = "/index", isExoteric = true)
public class UserCenter {
//
//	@Override
//	protected void doRender(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//		if (ctx.isResource()) {
//			byte[] b = ctx.resource(frame.path());
//			ctx.circuit().content().writeBytes(b);
//			return;
//		}
//
//		// Element panel = ctx.panel().e();
//		// Document doc = ctx.html("/user-manager.html",
//		// String.format("/%s", chipInfo().getId()), "utf-8");
//		// Elements es = doc.body().children();//
//		// 需要将样式标签传出来拼合，样式文件也一样，这是因为当前的panel仅是元素，而读取时虽然在头.
//		// if (es.isEmpty())
//		// return;
//		// Element e = es.first();
//		// panel.appendChild(e);
//		// ctx.panel().head().add(doc.head());
//
//	}
//
//	@Override
//	protected void doAjax(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//		String action = frame.parameter("action");
//		if ("selectUsers".equals(action)) {
//			selectUsers(user, frame, ctx);
//			return;
//		}
//
//	}
//
//	private void selectUsers(ISwsContext user, Frame frame, IControlContext ctx)
//			throws CircuitException {
//		Document doc = ctx.html("/select-user-popup.html", chipInfo().getId(),
//				"utf-8");
//		Elements es = doc.head().getElementsByTag("style");
//		if (!es.isEmpty()) {
//			doc.body().prependChild(es.first());
//		}
//		Elements ess = doc.head().getElementsByTag("script");
//		if (!ess.isEmpty()) {
//			doc.body().prependChild(ess.first());
//		}
//		String part = doc.body().html();
//		ctx.circuit().content().writeBytes(part.getBytes());
//	}

}
