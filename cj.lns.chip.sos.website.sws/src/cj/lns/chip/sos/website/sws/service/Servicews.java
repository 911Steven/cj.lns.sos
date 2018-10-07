package cj.lns.chip.sos.website.sws.service;

import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.sws.ServicewsSummary;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.MenuSO;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.sws.IServicews;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "servicews")
public class Servicews implements IServicews {
	@CjServiceRef(refByName = "securityService")
	private ISecurityService security;

	@Override
	public int authServicews(ISubject subject, String swsid,
			Map<String, Object> fillSws, Circuit circuit)
					throws CircuitException {
		return security.hasSwsVisitPermission(subject.principal(), swsid,
				fillSws);
	}

	@Override
	public List<AppSO> getMountedApp(String swsid) {
		Frame f = new Frame(String.format(
				"getMountedApps /sws/market/app/?swsId=%s sos/1.0", swsid));
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
				byte[] b = back.content().readFully();
				String json = new String(b);
				List<AppSO> apps = new Gson().fromJson(json,
						new TypeToken<List<AppSO>>() {
						}.getType());
				return apps;
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
		return null;
	}

	@Override
	public void unmountApp(String swsId, String provider, String appId) {
		Frame f = new Frame(String
				.format("unmountApp /sws/market/app/?swsId=%s sos/1.0", swsId));
		f.parameter("provider", provider);
		f.parameter("appCode", appId);
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	@Override
	public void mountApp(String swsId, byte[] appjson) {
		Frame f = new Frame(String
				.format("mountApp /sws/market/app/?swsId=%s sos/1.0", swsId));
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			f.content().writeBytes(appjson);
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}

	}

	@Override
	public List<PortletSO> getMountedPortlet(String swsid) {
		Frame f = new Frame(String.format(
				"getMountedPortlets /sws/market/portlet/?swsId=%s sos/1.0",
				swsid));
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
				byte[] b = back.content().readFully();
				String json = new String(b);
				List<PortletSO> lets = new Gson().fromJson(json,
						new TypeToken<List<PortletSO>>() {
						}.getType());
				return lets;
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
		return null;
	}

	@Override
	public void unmountPortlet(String swsid, String provider,
			String portletId) {
		Frame f = new Frame(String.format(
				"unmountPortlet /sws/market/portlet/?swsId=%s sos/1.0", swsid));
		f.parameter("provider", provider);
		f.parameter("code", portletId);
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	@Override
	public void mountPortlet(String swsid, byte[] portletjson) {
		Frame f = new Frame(String.format(
				"mountPortlet /sws/market/portlet/?swsId=%s sos/1.0", swsid));
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			f.content().writeBytes(portletjson);
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	@Override
	public List<MenuSO> getMountedMenus(String swsid) {
		Frame f = new Frame(String.format(
				"getMountedMenus /sws/market/menu/?swsId=%s sos/1.0", swsid));
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
				byte[] b = back.content().readFully();
				String json = new String(b);
				List<MenuSO> menus = new Gson().fromJson(json,
						new TypeToken<List<MenuSO>>() {
						}.getType());
				return menus;
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
		return null;
	}

	@Override
	public void unmountMenu(String swsid, String provider, String menuId) {
		Frame f = new Frame(String.format(
				"unmountMenu /sws/market/menu/?swsId=%s sos/1.0", swsid));
		f.parameter("provider", provider);
		f.parameter("code", menuId);
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	@Override
	public void mountMenu(String swsid, byte[] menujson) {
		Frame f = new Frame(String
				.format("mountMenu /sws/market/menu/?swsId=%s sos/1.0", swsid));
		f.contentType("json");
		f.contentChartset("utf-8");
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			f.content().writeBytes(menujson);
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
			}
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	@Override
	public SosUserInfo getOwner(String swsid) {
		Frame f = new Frame("getOwnerInfo /sws/owner sos/1.0");
		f.parameter("swsid", swsid);
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
		// 等待15秒
		try {
			IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
			m.site().out().flow(f, c);
			if ("frame/bin".equals(c.contentType())) {
				Frame back = new Frame(c.content().readFully());
				if (!"200".equals(back.head("status"))) {
					throw new CircuitException(back.head("status"), String
							.format("在远程服务器上出现错误。原因：%s", back.head("message")));
				}
				String json = new String(back.content().readFully());
				SosUserInfo ui = new Gson().fromJson(json, SosUserInfo.class);
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}

	@Override
	public ServicewsSummary getServicewsSummary(String swsid,
			IServiceosWebsiteModule m) {
		Frame f = new Frame("getServicewsSummary /sws/instance sos/1.0");
		f.parameter("swsid", swsid);
		Circuit c = new Circuit("sos/1.0 200 OK");
		// f.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC_TIMEOUT, "15000");//
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
				ServicewsSummary ui = new Gson().fromJson(json,
						ServicewsSummary.class);
				return ui;
			}
			return null;
		} catch (CircuitException e1) {
			throw new EcmException(e1);
		}
	}
}
