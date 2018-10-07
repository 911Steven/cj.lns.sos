package cj.lns.chip.sos.website.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.framework.IDatabaseCloud;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFlowContent;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.WebUtil;
import cj.studio.ecm.net.web.page.Page;
import cj.studio.ecm.net.web.page.context.PageContext;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
@CjService(name="/pages/registerUser.service")
public class RegisterUser  extends Page{
	@CjServiceRef(refByName="databaseCloud")
	IDatabaseCloud db;
	@Override
	public void doPage(Frame frame,Circuit circuit, IPlug plug, PageContext ctx)
			throws CircuitException {
		String b = new String(frame.content().readFully());
		try {
			b = URLDecoder.decode(b, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new CircuitException("503", e.getMessage());
		}
		Map<String, Object> map = WebUtil.parserParam(b);
		registerUser(map);
	}
	
	private void registerUser(Map<String, Object> map) throws CircuitException {
		Frame frame = new Frame("register /public/user/ sos/1.0");
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		frame.parameter("userCode",(String) map.get("userCode"));
		frame.parameter("nickName",(String) map.get("nickName"));
		frame.parameter("password",(String) map.get("password"));
		frame.parameter("capcity", (String)map.get("capcity"));
		frame.parameter("homeSize",(String) map.get("homeSize"));
		frame.parameter("faceImg",(String) map.get("faceImg"));
		frame.parameter("sex",(String) map.get("sex"));
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				try{
				delUser((String)map.get("userCode"));
				}catch(Exception e){
					
				}
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
		}
	}

	public Object viewUserByCode(String user) {
		Frame f = new Frame("findUserResources /user/manager sos/1.0");
		f.parameter("userCode", String.valueOf(user));
		Circuit c = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		try {
			m.site().out().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			 String json = new String(back.content().readFully());
			 return new Gson().fromJson(json, new TypeToken<HashMap<String,Object>>() {
			 }.getType());
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
		 return null;
	}
	@SuppressWarnings("unchecked")
	public void delUser(String user) {
		Object o = viewUserByCode(user);
		if (o == null) {
			return;
		}
		Map<String, Object> d = (Map<String, Object>) o;
		Map<String, Object> u = (Map<String, Object>) d.get("user");
		List<Map<String, Object>> s = (List<Map<String, Object>>) d
				.get("swsList");
		for(Map<String, Object> sws:s){
			delSws(String.valueOf((long)(double)sws.get("id")));
		}
		delUserEntity((String)u.get("userCode"));
	}
	private void delUserEntity(String user) {
		Frame f = new Frame("removeUser /user/manager sos/1.0");
		f.parameter("userCode", String.valueOf(user));
		Circuit c = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		try {
			m.site().out().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
	}
	public void delSws(String swsid) {
		Frame f = new Frame("deleteSws /sws/build sos/1.0");
		f.parameter("swsid", swsid);
		Circuit c = new Circuit("sos/1.0 200 ok");
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		try {
			m.site().out().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}

		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
	}
}
