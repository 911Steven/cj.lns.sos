package cj.lns.chip.sos.supersws.core;

import java.util.List;

import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPin;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

public class Supersws {
	SwsInfo supersws;
	IPin out;

	public Supersws(SwsInfo supersws, IPin out) {
		this.supersws = supersws;
		this.out = out;
	}

	public SwsInfo getSupersws() {
		return supersws;
	}

	public List<SwsInfo> getAllBasicSws() {
		Frame f = new Frame("getAllBasicsws /framework/info sos/1.0");
		f.parameter("inheritId",supersws.getId().toString());
		f.parameter("portal",supersws.getUsePortal());
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			out.flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json, new TypeToken<List<SwsInfo>>() {
			}.getType());
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
		return null;
	}

}
