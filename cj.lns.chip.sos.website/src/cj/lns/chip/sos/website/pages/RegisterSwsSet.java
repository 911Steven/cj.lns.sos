package cj.lns.chip.sos.website.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoClient;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.disk.NetDisk;
import cj.lns.chip.sos.website.framework.IDatabaseCloud;
import cj.lns.common.sos.service.model.sws.SwsInfo;
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

@CjService(name = "/pages/registerSwsSet.service")
public class RegisterSwsSet extends Page {
	@CjServiceRef(refByName="databaseCloud")
	IDatabaseCloud db;

	@Override
	public void doPage(Frame frame, Circuit circuit, IPlug plug,
			PageContext ctx) throws CircuitException {
		String b = new String(frame.content().readFully());
		try {
			b = URLDecoder.decode(b, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new CircuitException("503", e.getMessage());
		}
		Map<String, Object> map = WebUtil.parserParam(b);
		String json = (String)map.get("json");
		Map<String, Object> map2 = new Gson().fromJson(json,
				new TypeToken<Map<String, Object>>() {
				}.getType());
		inheritSws(map2);

		double userState = (double) map2.get("userState");
		@SuppressWarnings("unchecked")
		Map<String, String> user = (Map<String, String>) map2.get("user");
		String userCode = user.get("userCode");
		updateUserState(userCode,(int) userState);
	}

	private void updateUserState(String userCode,int userState) throws CircuitException {
		Frame frame = new Frame("updateState /public/user/ sos/1.0");
		frame.parameter("state",String.valueOf(userState));
		frame.parameter("userCode",userCode);
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		Frame back = new Frame(ctx.readFully());
		int state = Integer.valueOf(back.head("status"));
		if (state != 200) {
			throw new CircuitException(back.head("status"),
					back.head("message"));
		}
	}

	@SuppressWarnings("unchecked")
	private void inheritSws(Map<String, Object> map) throws CircuitException {
		// {swsSet[list][1][swsid]=6666666199, swsSet[list][1][swstid]=7,
		// swsSet[list][0][swstid]=1, swsSet[list][0][swsSize]=2, userState=1,
		// swsSet[size]=4, swsSet[list][0][swsid]=6666666200,
		// swsSet[list][1][swsSize]=2}
		Map<String, String> user = (Map<String, String>) map.get("user");
		String userCode = user.get("userCode");
		String password = user.get("password");

		MongoClient client = this.db.userClient();
		INetDisk disk = NetDisk.open(client, userCode, userCode, password);
		Map<String, Object> swsSet = (Map<String, Object>) map.get("swsSet");
		List<Map<String, Object>> list = (List<Map<String, Object>>) swsSet
				.get("list");
		for (Map<String, Object> sws : list) {
			String newSwsId = (String) sws.get("swsid");
			String templateSwsId = (String) sws.get("swstid");
			double swsSize = (double) sws.get("swsSize");
			swsSize = swsSize * 1024 * 1024 * 1024;

			IPin out = ServiceosWebsiteModule.get().out();
			Frame frame = new Frame("assignNewSws /sws/instance sos/1.0");
			Circuit circuit = new Circuit("sos/1.0 200 ok");
			frame.parameter("userCode", userCode);
			frame.parameter("swsId", newSwsId);
			frame.parameter("inheritId", templateSwsId);
			out.flow(frame, circuit);
			IFlowContent ctx = circuit.content();
			if (ctx.readableBytes() > 0) {
				Frame back = new Frame(ctx.readFully());
				int state = Integer.valueOf(back.head("status"));
				if (state != 200) {
					throw new CircuitException(back.head("status"),
							back.head("message"));
				}
				String json = new String(back.content().readFully());
				SwsInfo swsinfo = new Gson().fromJson(json, SwsInfo.class);

				CubeConfig conf = new CubeConfig(swsSize);
				conf.alias(swsinfo.getName());
				conf.setDesc(swsinfo.getDescription());
				disk.createCube(newSwsId, conf);
			}
		}

	}
}
