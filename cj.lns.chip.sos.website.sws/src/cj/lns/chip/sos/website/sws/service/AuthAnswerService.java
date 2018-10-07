package cj.lns.chip.sos.website.sws.service;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.service.sws.security.AuthAskInfo;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
@CjService(name="authAnswerService")
public class AuthAnswerService implements IAuthAnswerService{
	@Override
	public void auth(String askId, String answer) throws CircuitException {
		Frame f = new Frame(
				"auth /sws/security/auth/answer sos/1.0");
		f.parameter("askId",askId);
		f.parameter("answer",answer);
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
		}
		
	}
	@Override
	public List<AuthAskInfo> getAskList(String swsid, String permId) throws CircuitException {
		Frame f = new Frame(
				"getAuthAsk /sws/security/auth/answer sos/1.0");
		f.parameter("swsid",swsid);
		f.parameter("permissionId",permId);
		Circuit c = new Circuit("sos/1.0 200 OK");
//		f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");// 等待15秒
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		m.site().out().flow(f, c);
		if ("frame/bin".equals(c.contentType())) {
			Frame frame = new Frame(c.content().readFully());
			if (!"200".equals(frame.head("status"))) {
				throw new CircuitException(frame.head("status"), String.format(
						"在远程服务器上出现错误。原因：%s %s",frame.head("status"), frame.head("message")));
			}
			String json=new String(frame.content().readFully());
			List<AuthAskInfo> list=new Gson().fromJson(json, new TypeToken<List<AuthAskInfo>>(){}.getType());
			return list;
		}
		return new ArrayList<>();
	}

}
