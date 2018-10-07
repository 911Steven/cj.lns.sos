package cj.lns.chip.sos.supersws.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;

import cj.lns.chip.sos.supersws.ISos;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.studio.ecm.IAssembly;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.net.IClient;
import cj.studio.ecm.net.IConnectCallback;
import cj.studio.ecm.net.graph.INetGraph;
import cj.studio.ecm.net.nio.NetConstans;
import cj.studio.ecm.net.rio.tcp.TcpCjNetClient;
import cj.studio.ecm.net.rio.udt.UdtCjNetClient;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "sos")
public class Sos implements ISos {

	private String homeDir;
	private IClient sosdb;
	private String cjtoken;
	private Map<String, Supersws> supers;

	@Override
	public void close() {
		sosdb.close();
	}

	@Override
	public void load(CommandLine line) {
		supers = new HashMap<>();

		// 初始化sosdb连接
		// 初始化netdisk连接
		String host = line.getOptionValue("h");
		String t = line.getOptionValue("t");
		this.cjtoken = line.getOptionValue("token");

		String[] arr = host.split(":");
		switch (t) {
		case "rio-tcp":
			sosdb = new TcpCjNetClient();
			break;
		case "rio-udt":
			sosdb = new UdtCjNetClient();
			break;
		default:
			System.out.println("不支持协议：" + t);
			return;
		}
		try {
			sosdb.connect(arr[0], arr[1], new IConnectCallback() {

				@Override
				public void buildGraph(Object owner, INetGraph ng) {
					ng.netInput().plugFirst("inputSink", new ISink() {

						@Override
						public void flow(Frame frame, Circuit circuit,
								IPlug plug) throws CircuitException {
							if (!frame.containsParameter("cjtoken")) {
								frame.parameter("cjtoken", cjtoken);
							}
							if (!frame.containsHead(
									NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC)) {
								frame.head(
										NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC,
										"true");
								frame.head(
										NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT,
										"150000");
							}
							frame.url(
									String.format("/serviceOS%s", frame.url()));
							plug.flow(frame, circuit);
						}
					});
					ng.netOutput().plugLast("outputSink", new ISink() {

						@Override
						public void flow(Frame frame, Circuit circuit,
								IPlug plug) throws CircuitException {
							plug.flow(frame, circuit);
							// System.out.println(String.format("回复：\r\n%s%s%s",SosConsole.COLOR_RESPONSE,new
							// String(frame.toBytes()),SosConsole.COLOR_SURFACE));
						}
					});
				}
			});
		} catch (InterruptedException e) {
			System.out.println("error:" + e);
		}
	}

	@Override
	public void setMain(IAssembly main) {
		// TODO Auto-generated method stub

	}

	@Override
	public Supersws getSupersws(String swsid) {
		List<SwsInfo> list = getSuperswsList();
		SwsInfo sup = null;
		for (SwsInfo si : list) {
			if (swsid.equals(si.getId().toString())) {
				sup = si;
				break;
			}
		}
		if (sup == null)
			return null;
		Supersws sws = new Supersws(sup, sosdb.buildNetGraph().netInput());
		this.supers.put(swsid, sws);
		return sws;
	}

	@Override
	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}

	@Override
	public List<SwsInfo> getSuperswsList() {
		Frame f = new Frame("getAllSupersws /framework/info sos/1.0");
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
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

	@Override
	public void deleteSupersws(String portal) {
		Frame f = new Frame("delSupersws /framework/info sos/1.0");
		f.parameter("portalId", portal);
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
	}

	@Override
	public String createSupersws(String name, String owner, String scene,
			String canvas, String theme, String desc) {
		Frame f = new Frame("createSupersws /framework/info sos/1.0");
		f.parameter("portalId", name);
		f.parameter("scene", scene);
		f.parameter("canvas", canvas);
		f.parameter("theme", theme);
		f.parameter("owner", owner);
		f.parameter("desc", desc);
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			String swsid = back.head("swsid");
			return swsid;
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
		return "";
	}

	@Override
	public String getVersion() {
		Frame f = new Frame("getSosVersion /framework/info sos/1.0");
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			String json = new String(back.content().readFully());
			return json;
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
		return null;
	}

	@Override
	public List<SwsInfo> findSwsListByLevel(int level, String skip,
			String limit) {
		Frame f = new Frame("findServicews /sws/instance sos/1.0");
		f.parameter("level", String.valueOf(level));
		f.parameter("skip", skip);
		f.parameter("limit", limit);
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
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

	@Override
	public void delSws(String swsid) {
		Frame f = new Frame("deleteSws /sws/build sos/1.0");
		f.parameter("swsid", swsid);
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}

		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
	}

	@Override
	public Object viewUserByCode(String user) {
		Frame f = new Frame("findUserResources /user/manager sos/1.0");
		f.parameter("userCode", String.valueOf(user));
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
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

	@Override
	public List<SosUser> findUserListByCode(String user, String skip,
			String limit) {
		Frame f = new Frame("findUserList /public/user/ sos/1.0");
		if (user != null && user.trim() != "") {
			f.parameter("userCode", user);
		}
		f.parameter("start", skip);
		f.parameter("max", limit);
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
			String json = new String(back.content().readFully());
			return new Gson().fromJson(json, new TypeToken<List<SosUser>>() {
			}.getType());
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
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
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
	}
	@Override
	public void addSosRoleToUser(String roleCode, String user) {
		Frame f = new Frame("addSosRoleToUser /user/manager sos/1.0");
		f.parameter("userCode", String.valueOf(user));
		f.parameter("roleCode", String.valueOf(roleCode));
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
			Frame back = new Frame(c.content().readFully());
			if (!"200".equals(back.head("status"))) {
				throw new CircuitException(back.head("status"),
						back.head("message"));
			}
		} catch (CircuitException e) {
			System.out.println("错误：" + e);
		}
	}
	@Override
	public void removeSosRoleToUser(String roleCode, String user) {
		Frame f = new Frame("removeSosRoleFromUser /user/manager sos/1.0");
		f.parameter("userCode", String.valueOf(user));
		f.parameter("roleCode", String.valueOf(roleCode));
		Circuit c = new Circuit("sos/1.0 200 ok");
		try {
			sosdb.buildNetGraph().netInput().flow(f, c);
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
