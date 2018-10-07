package cj.lns.chip.sos.website.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;

import com.mongodb.MongoClient;

import cj.lns.chip.sos.cube.framework.CubeConfig;
import cj.lns.chip.sos.disk.DiskInfo;
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

@CjService(name = "/pages/register.service")
public class RegisterServicePage extends Page {
	@CjServiceRef(refByName="databaseCloud")
	IDatabaseCloud db;

	@Override
	protected void doPage(Frame frame, Circuit circuit, IPlug plug,
			PageContext ctx) throws CircuitException {
		String action = frame.parameter("action");
		if ("genSwsNum".equals(action)) {
			byte[] b = frame.content().readFully();
			String params = new String(b);
			Map<String, Object> map = WebUtil.parserParam(params);
			genSwsNum((String)map.get("assignedNum"), circuit);
		} else if ("postData".equals(action)) {
			// data[user][userCode]=carocean&data[user][nickName]=王八蛋&data[user][password]=jofers0408&data[user][sex]=female&data[user][photoUrl]=&data[swsSet][newSwsIds][1]=666666687&data[swsSet][newSwsIds][7]=666666686
			String b = new String(frame.content().readFully());
			try {
				b = URLDecoder.decode(b, "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new CircuitException("503", e.getMessage());
			}
			Map<String, Object> map = WebUtil.parserParam(b);
			registerUser(map);
			inheritSws(map);
		}
	}

	private void inheritSws(Map<String, Object> map) throws CircuitException {
		String userCode = (String)map.get("data[user][userCode]");
		String password = (String)map.get("data[user][password]");
		MongoClient client = this.db.userClient();
		INetDisk disk=NetDisk.open(client, userCode,userCode,password);
		Set<String> keys = map.keySet();
		for (String key : keys) {
			String newSwsId =(String) map.get(key);
			if (!key.startsWith("data[swsSet][newSwsIds]")) {
				continue;
			}

			String k = key.substring("data[swsSet][newSwsIds]".length(),
					key.length());
			String templateSwsId = k.trim().substring(1, k.length() - 1);
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
				SwsInfo sws = new Gson().fromJson(json, SwsInfo.class);
				
				CubeConfig conf=new CubeConfig(-1);
				conf.alias(sws.getName());
				conf.setDesc(sws.getDescription());
				disk.createCube(newSwsId, conf);
			}
		}

	}

	private void registerUser(Map<String, Object> map) throws CircuitException {
		Frame frame = new Frame("register /public/user/ sos/1.0");
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		frame.parameter("userCode",(String) map.get("data[user][userCode]"));
		frame.parameter("nickName",(String) map.get("data[user][nickName]"));
		frame.parameter("password",(String) map.get("data[user][password]"));
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			CubeConfig conf = new CubeConfig(-1);
			conf.alias(frame.parameter("nickName"));
			DiskInfo di = new DiskInfo(frame.parameter("userCode"), conf);
			MongoClient client = this.db.userClient();
			NetDisk.create(client, frame.parameter("userCode"),
					frame.parameter("userCode"), frame.parameter("password"),
					di);
		}
	}

	private void genSwsNum(String assignedNum, Circuit client)
			throws CircuitException {
		Frame frame = new Frame("genServicewsNum /framework/idgen/ sos/1.0");
		Circuit circuit = new Circuit("sos/1.0 200 ok");
		frame.parameter("assignedNum", assignedNum);
		IPin out = ServiceosWebsiteModule.get().out();
		out.flow(frame, circuit);
		IFlowContent ctx = circuit.content();
		if (ctx.readableBytes() > 0) {
			Frame back = new Frame(ctx.readFully());
			int state = Integer.valueOf(back.head("status"));
			if (state != 200) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			String json = String.format(
					"{\"wholeNum\":\"%s\",\"reedomNum\":\"%s\"}",
					back.head("wholeNum"), back.head("reedomNum"));
			client.content().writeBytes(json.getBytes());
		}
	}
}
