package cj.lns.chip.sos.website.sws.component.market;

import cj.lns.chip.sos.website.MenuSO;
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

@CjService(name = "/market/mountMenu.html")
public class MountMenuComponent implements IComponent {
	@CjServiceRef(refByName = "servicews")
	IServicews sws;
	@Override
	public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
		String action=frame.parameter("action");
		String menuId=frame.parameter("menuId");
		String provider=frame.parameter("provider");//提供商指应用提供器所在芯片的标识
		String category=frame.parameter("category");
		if("mount".equals(action)){
			mountMenu(category,provider,menuId,frame,circuit);
			return;
		}
		if("unmount".equals(action)){
			unmountMenu(category,provider,menuId,frame,circuit);
			return;
		}
	}
	private void unmountMenu(String category, String provider, String menuId, Frame frame, Circuit circuit)throws CircuitException  {
		IServicewsContext ctx=IServicewsContext.context(frame);
		sws.unmountMenu(ctx.swsid(),provider,menuId);
	}
	private void mountMenu(String category,String provider, String menuId, Frame frame, Circuit circuit)throws CircuitException  {
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		String url="/portal/market/findMenu.service";
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
		MenuSO menu=new Gson().fromJson(json, MenuSO.class);
		if(!menu.getSosPermissions().contains("mountable")){
			throw new CircuitException("802", "您没有安装权限");
		}
		IServicewsContext ctx=IServicewsContext.context(frame);
		sws.mountMenu(ctx.swsid(),b);
	}	

}
