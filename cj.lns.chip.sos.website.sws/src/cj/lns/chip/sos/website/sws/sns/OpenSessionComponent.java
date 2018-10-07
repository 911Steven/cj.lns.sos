package cj.lns.chip.sos.website.sws.sns;

import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IServiceSetter;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.nio.NetConstans;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;
/**
 * 用法，新增一种应用则需要实现一种opener
 * <pre>
 * 然后用反射注入到此服务中
 * </pre>
 * @author carocean
 *
 */
@CjService(name = "/session/openSession.html")
public class OpenSessionComponent implements IComponent,IServiceSetter {
	Map<String,IAppSessionOpener> openers;
	public OpenSessionComponent() {
		this.openers=new HashMap<>();
	}
	@Override
	public void setService(String serviceId, Object service) {
		openers.put(serviceId, (IAppSessionOpener)service);
	}
	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		String sid = frame.parameter("sid");
		if (StringUtil.isEmpty(sid)) {
			throw new CircuitException("404", "缺少参数:sid");
		}
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		
		IServicewsContext sws = IServicewsContext.context(frame);
		
		
		// 加载用户列表、加载未读消息

		Map<String, Object> session = getSession(sws.swsid(), sws.owner(),
				sid, m);// 得到会话
		@SuppressWarnings("unchecked")
		Map<String, String> app = (Map<String, String>) session.get("snsApp");
		String appCode = app.get("code");
		if(!openers.containsKey(appCode)){
			throw new CircuitException("503",
					String.format("不支持的应用:%s", appCode));
		}
		IAppSessionOpener opener=openers.get(appCode);
		opener.flow(session,app,frame,circuit);
		
	}

	public Map<String, Object> getSession(String swsid, String uid, String sid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("open /device/session.service peer/1.0");
		f.parameter("swsid", swsid);
		f.parameter("uid", uid);
		f.parameter("sid", sid);
		Circuit c = new Circuit("peer/1.0 200 OK");
		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
				String json = new String(back.content().readFully());
				Map<String, Object> ui = new Gson().fromJson(json,
						new TypeToken<HashMap<String, Object>>() {
						}.getType());
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}
}
