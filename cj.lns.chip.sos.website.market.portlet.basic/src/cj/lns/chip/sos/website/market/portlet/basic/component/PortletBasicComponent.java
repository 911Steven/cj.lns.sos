package cj.lns.chip.sos.website.market.portlet.basic.component;

import org.jsoup.nodes.Document;

import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name="/")
public class PortletBasicComponent implements IComponent {

	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		if(frame.content().readableBytes()<1){
			return;
		}
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		String json=new String(frame.content().readFully());
		PortletSO let=new Gson().fromJson(json, PortletSO.class);
		if("news".equals(let.getId())){
			Document doc=m.context().html("/index.html",m.site().contextPath(),"utf-8");
			circuit.content().writeBytes(doc.toString().getBytes());
		}
	}

}
