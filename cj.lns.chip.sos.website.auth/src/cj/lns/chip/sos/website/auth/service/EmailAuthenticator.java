package cj.lns.chip.sos.website.auth.service;

import cj.lns.chip.sos.website.framework.IAuthForm;
import cj.lns.chip.sos.website.framework.IAuthenticator;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

/**
 * 视窗持有者以自定义代码登录认证
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
//认证服务名称定义规则是：AccountType的类型后面加上Auth
@CjService(name = "emailAuth")
public class EmailAuthenticator implements IAuthenticator {

	@Override
	public void authenticate(IAuthForm form) throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		Frame frame = new Frame(
				String.format("authUserCode /public/auth/?account=%s&password=%s&cjtoken=%s sos/1.0",
						form.get("account"), form.get("password"), form.get("cjtoken")));
		Circuit circuit = new Circuit(String.format("sos/1.0 200 ok"));
		m.site().out().flow(frame, circuit);
		IFlowContent cnt = circuit.content();
		if (cnt.readableBytes() > 0) {
			Frame back = new Frame(cnt.readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			form.put("hasDefaultSwsId", back.head("hasDefaultSwsId"));
			form.put("defaultSwsId", back.head("defaultSwsId"));
			byte[] b=back.content().readFully();
			JsonElement e=new Gson().fromJson(new String(b), JsonElement.class);
			JsonArray ja=e.getAsJsonArray();
			String roles="";
			for(JsonElement je:ja){
				JsonObject jo=je.getAsJsonObject();
				roles+=String.format("%s,",ElementGet.getJsonProp(jo.get("code")));
			}
			if(roles.endsWith(",")){
				roles=roles.substring(0, roles.length()-1);
			}
			form.put("sos.roles", roles);
		} else {
			throw new CircuitException("503", "远程服务器返回空的认证信息");
		}
	}

}
