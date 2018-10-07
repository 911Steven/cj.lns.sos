package cj.lns.chip.sos.website.portlet.service;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
@CjService(name="swsPortletRenderService")
public class SwsProtletRenderService implements ISwsPortletRenderService{

	@Override
	public void renderToServicews(Frame frame, Circuit circuit) throws CircuitException {
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		String old=frame.url();
		String url="/servicews/portletRender.html";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		try{
		m.site().out().flow(frame, circuit);
		}catch(Exception e){
			throw e;
		}finally{
			frame.url(old);
		}
	}

}
