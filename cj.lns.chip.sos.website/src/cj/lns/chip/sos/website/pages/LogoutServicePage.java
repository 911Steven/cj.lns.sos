package cj.lns.chip.sos.website.pages;

import cj.lns.chip.sos.website.framework.IServiceosContext;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.layer.ISession;
import cj.studio.ecm.net.web.HttpFrame;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;
@CjService(name="/public/logout.html")
public class LogoutServicePage  extends Page{
	@Override
	public void doPage(Frame frame,Circuit circuit, IPlug plug, PageContext ctx)
			throws CircuitException {
		HttpFrame hf=(HttpFrame)frame;
		ISession session=hf.session();
		session.removeAttribute(ISubject.KEY_SUBJECT_IN_SESSION);
		session.removeAttribute(IServiceosContext.KEY_SERVICEOSCONTEXT_IN_SESSION);
		session.removeAttribute(IServicewsContext.KEY_SWS_CONTEXT_IN_SESSION);
	}
}
