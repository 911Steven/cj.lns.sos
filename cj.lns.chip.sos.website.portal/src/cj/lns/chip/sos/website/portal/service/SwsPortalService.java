package cj.lns.chip.sos.website.portal.service;

import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.portal.ISwsPortalService;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

@CjService(name="swsPortalService")
public class SwsPortalService implements ISwsPortalService{

	@Override
	public Map<String, String> getConfig(String swsid) throws CircuitException {
		Frame f = new Frame(
				"getServicePortalConfig /sws/instance sos/1.0");
		f.parameter("swsid",swsid);
		Circuit c = new Circuit("sos/1.0 200 OK");
//		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");// 等待15秒
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(f, c);
		if ("frame/bin".equals(c.contentType())) {
			Frame frame = new Frame(c.content().readFully());
			if (!"200".equals(frame.head("status"))) {
				throw new CircuitException(frame.head("status"), String.format(
						"认证失败。原因：%s %s",frame.head("status"), frame.head("message")));
			}
			String json =new String(frame.content().readFully());
			JsonElement je=new Gson().fromJson(json, JsonElement.class);
			JsonObject jo=je.getAsJsonObject();
			Map<String, String> map=new HashMap<>();
			for(Map.Entry<String,JsonElement> en:jo.entrySet()){
				String v=ElementGet.getJsonProp(en.getValue());
				map.put(en.getKey(), v);
			}
			return map;
		}
		return new HashMap<>();
	}

}
