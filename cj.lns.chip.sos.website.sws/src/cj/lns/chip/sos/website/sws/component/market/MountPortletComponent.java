package cj.lns.chip.sos.website.sws.component.market;

import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjService(name = "/market/mountPortlet.html")
public class MountPortletComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String action=frame.parameter("action");
		String letId=frame.parameter("portletId");
		String provider=frame.parameter("provider");//提供商指应用提供器所在芯片的标识
		String category=frame.parameter("category");
		if("mount".equals(action)){
			mountPortlet(category,provider,letId,frame,circuit);
			return;
		}
		if("unmount".equals(action)){
			unmountPortlet(category,provider,letId,frame,circuit);
			return;
		}
	}
	private void unmountPortlet(String category, String provider, String letId, Frame frame, Circuit circuit)throws CircuitException  {
		IServicewsContext ctx=IServicewsContext.context(frame);
		sws.unmountPortlet(ctx.swsid(),provider,letId);
	}
	private void mountPortlet(String category,String provider, String letId, Frame frame, Circuit circuit)throws CircuitException  {
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		String url="/portal/market/findPortlet.service";
		if (frame.containsQueryString()) {
			url = String.format("%s?%s", url, frame.queryString());
		}
		frame.url(url);
		m.site().out().flow(frame, circuit);
		if("404".equals(circuit.status())){
			throw new CircuitException("404", circuit.message());
		}
		byte[] b=circuit.content().readFully();
		String json=new String(b);
		PortletSO let=new Gson().fromJson(json, PortletSO.class);
		if(!let.getSosPermissions().contains("mountable")){
			throw new CircuitException("802", "您没有安装权限");
		}
		IServicewsContext ctx=IServicewsContext.context(frame);
		sws.mountPortlet(ctx.swsid(),b);
	}	

}
