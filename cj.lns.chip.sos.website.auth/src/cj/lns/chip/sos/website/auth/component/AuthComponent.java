package cj.lns.chip.sos.website.auth.component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.website.auth.AccountType;
import cj.lns.chip.sos.website.auth.AuthForm;
import cj.lns.chip.sos.website.auth.IAuthFactory;
import cj.lns.chip.sos.website.auth.ServiceosContext;
import cj.lns.chip.sos.website.auth.Subject;
import cj.lns.chip.sos.website.framework.Face;
import cj.lns.chip.sos.website.framework.IAuthForm;
import cj.lns.chip.sos.website.framework.IAuthenticator;
import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.framework.RemoteDevice;
import cj.lns.common.sos.website.customable.IComponent;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.net.web.HttpFrame;

/**
 * 认证组件
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "/auth")
public class AuthComponent implements IComponent {
	@CjServiceRef(refByName = "authFactory")
	IAuthFactory factory;

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		if (frame.content().readableBytes() > 0) {
			String text = new String(frame.content().readFully());
			Map<String, String> map = null;
			try {
				String kvStr = URLDecoder.decode(text, "utf-8");
				map = parserParam(kvStr);
			} catch (UnsupportedEncodingException e) {
			}
			if (map == null) {
				throw new CircuitException("404", "登录缺少必要参数");
			}
			IAuthForm form = new AuthForm();
			form.load(map);
			AccountType type = factory.recognize(form);
			IAuthenticator a = factory.authenticator(type);
			a.authenticate(form);
			if (frame instanceof HttpFrame) {
				HttpFrame f = (HttpFrame) frame;
				Face face=(Face)form.get("face");
				Subject subject = new Subject((String)form.get("account"),
						(String)form.get("sos.roles"),face);
				f.session().attribute(ISubject.KEY_SUBJECT_IN_SESSION, subject);
				ServiceosContext ctx = new ServiceosContext();
				f.session().attribute(
						IServiceosContext.KEY_SERVICEOSCONTEXT_IN_SESSION, ctx);
				RemoteDevice rd = (RemoteDevice) RemoteDevice.parse(frame);
				f.session().attribute(IRemoteDevice.KEY_REMOTEDEVICE_IN_SESSION,
						rd);
			}
			String json = String.format(
					"{'hasDefaultSws':'%s','defaultSwsId':'%s','rootName':'%s'}",
					form.get("hasDefaultSwsId"), form.get("defaultSwsId"),
					frame.head("root-name"));
			circuit.content().writeBytes(json.getBytes());
		}
	}

	private Map<String, String> parserParam(String data) {
		Map<String, String> map = new HashMap<>();
		String[] kvpair = data.split("&");
		for (String kvStr : kvpair) {
			String[] kv = kvStr.split("=");
			String k = kv[0];
			String v = kv.length > 1 ? kv[1] : "";
			map.put(k, v);
		}
		return map;
	}

}
