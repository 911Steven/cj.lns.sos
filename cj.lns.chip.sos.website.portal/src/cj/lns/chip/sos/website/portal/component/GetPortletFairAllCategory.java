package cj.lns.chip.sos.website.portal.component;

import cj.lns.chip.sos.website.portal.MarketCenter;
import cj.lns.chip.sos.website.portal.market.IDeviceMarket;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletCategory;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletFair;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonObject;
@CjService(name="/market/getPortletFairAllCategory.service")
public class GetPortletFairAllCategory implements IComponent{
	@CjServiceRef(refByName="marketCenter")
	MarketCenter market;
	@Override
	public void flow(Frame frame, Circuit circuit,IPlug plug) throws CircuitException {
		IDeviceMarket device=market.device(frame);
		IPortletFair fair=device.getPortletFair();
		String arr[] =fair.enumCategory();
		JsonArray ja=new JsonArray();
		for(String categoryId:arr){
			JsonObject jo=new JsonObject();
			IPortletCategory ac=fair.category(categoryId);
			jo.addProperty("categoryId", categoryId);
			jo.addProperty("categoryName", ac.getCategoryName());
			jo.addProperty("categoryDesc", ac.getCategoryDesc());
			ja.add(jo);
		}
		circuit.content().writeBytes(ja.toString().getBytes());
	}

}
