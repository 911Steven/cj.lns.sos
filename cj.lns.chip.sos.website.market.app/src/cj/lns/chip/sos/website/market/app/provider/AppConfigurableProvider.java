package cj.lns.chip.sos.website.market.app.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

/**
 * 可配置的应用提供器
 * 
 * <pre>
 * ~ 必须声明为服务
 * ~ 支持配置文件
 * ~ 在服务初始化后加载配置文件
 * ~ 在应用程序的Modulepoint中装载该提供器
 * </pre>
 * 
 * @author carocean
 *
 */
public abstract class AppConfigurableProvider implements IAppProvider {
	private String categoryId;
	private List<AppSO> apps;
	private Map<String, String[]> acl;

	protected List<AppSO> apps() {
		return apps;
	}

	public void init(IServiceosWebsiteModule m) {
		try {
			byte[] b = m.context().resource(getConfigJsonFile());
			String json = new String(b);
			JsonElement je = new Gson().fromJson(json, JsonElement.class);
			JsonObject jo = je.getAsJsonObject();
			categoryId = ElementGet.getJsonProp(jo.get("category"));
			acl = new HashMap<>();
			JsonArray ja = jo.get("apps").getAsJsonArray();
			apps = new ArrayList<>();
			for (JsonElement jeapp : ja) {
				JsonObject joapp = jeapp.getAsJsonObject();
				AppSO app = new Gson().fromJson(joapp, AppSO.class);
				app.setProvider(m.chip().info().getId());
				app.setCategory(categoryId);
				apps.add(app);
				List<String> aclList = new ArrayList<>();
				JsonElement jeacl = joapp.get("acl");
				if (jeacl == null) {
					continue;
				}
				JsonArray jaacl = jeacl.getAsJsonArray();
				for (JsonElement jacl : jaacl) {
					String item = jacl.getAsString();
					String[] arr = item.split(" ");
					if (arr.length != 3) {
						throw new CircuitException("503", String
								.format("应用%s的ACL项配置错误:%s", app.getId(), item));
					}
					aclList.add(item);
				}
				acl.put(app.getId(), aclList.toArray(new String[0]));
			}
		} catch (CircuitException e) {
			throw new EcmException(String.format("装载应用提供器失败，原因：%s %s",
					e.getStatus(), e.getMessage()));
		}
	}

	/**
	 * 配置文件
	 * 
	 * <pre>
	 * 默认文件在 resources/fair-app.plugin.json
	 * </pre>
	 * 
	 * @return
	 */
	protected String getConfigJsonFile() {
		return "/app.fair.plugin.json";
	}

	@Override
	public String category() {
		return categoryId;
	}

	@Override
	public AppSO findApp(ISubject subject, String appId) {
		AppSO found = null;
		for (AppSO a : apps) {
			if (appId.equals(a.getId())) {
				found = a;
				break;
			}
		}
		if (found == null) {
			return null;
		}
		String[] aclArr = acl.get(appId);
		List<String> permissions = found.getSosPermissions();
		for (String ace : aclArr) {
			String[] arr = ace.split(" ");
			if ("allow".equals(arr[0])) {
				if (subject.containsRole(arr[1])
						&& !permissions.contains(arr[2])) {
					permissions.add(arr[2]);
				}
				continue;
			}
			if ("deny".equals(arr[0])) {
				if (subject.containsRole(arr[1])) {
					permissions.remove(arr[2]);
				}
			}
		}
		if (permissions.contains("visible")) {
			return found;
		}
		return null;
	}

	@Override
	public List<AppSO> getApps(ISubject subject, IServicewsContext sws) {
		List<AppSO> ret = new ArrayList<>();
		for (AppSO app : apps) {
			if (!this.acl.containsKey(app.getId()))
				continue;
			String[] acl = this.acl.get(app.getId());
			List<String> permissions = app.getSosPermissions();
			permissions.clear();// 重新计算权限。由于app是全局的，如果不清除可能会是上次计算的结果
			for (String ace : acl) {
				String[] arr = ace.split(" ");
				if (arr.length != 3) {
					throw new EcmException(String.format(
							"授权不符合格式，检查中间是否缺空格或有连接多个空格，正确规范：allow allUsers visible. 错误在：%s in %s",
							app.getId(), app.getProvider()));
				}
				if ("allow".equals(arr[0])) {
					if (arr[1].startsWith("$level")) {
						String express = arr[1].substring(6, arr[1].length());
						String value = "";
						if (express.startsWith(">=")) {
							value = express.substring(2, express.length());
							if (sws.level()>=Byte.valueOf(value) ) {
								permissions.add(arr[2]);
							}
						} else if (express.startsWith("<=")) {
							value = express.substring(2, express.length());
							if (sws.level()<=Byte.valueOf(value)) {
								permissions.add(arr[2]);
							}
						} else if (express.startsWith("<")) {
							value = express.substring(1, express.length());
							if (sws.level()<Byte.valueOf(value)) {
								permissions.add(arr[2]);
							}
						} else if (express.startsWith(">")) {
							value = express.substring(1, express.length());
							if (sws.level()>Byte.valueOf(value) ) {
								permissions.add(arr[2]);
							}
						} else {// =
							value = express.substring(1, express.length());
							if (Byte.valueOf(value) == sws.level()) {
								permissions.add(arr[2]);
							}
						}
					} else {
						if (subject.containsRole(arr[1])
								&& !permissions.contains(arr[2])) {
							permissions.add(arr[2]);
						}
					}
				} else if ("deny".equals(arr[0])) {
					if (arr[1].startsWith("$level")) {
						String express = arr[1].substring(6, arr[1].length());
						String value = "";
						if (express.startsWith(">=")) {
							value = express.substring(2, express.length());
							if (sws.level()>=Byte.valueOf(value)) {
								permissions.remove(arr[2]);
							}
						} else if (express.startsWith("<=")) {
							value = express.substring(2, express.length());
							if (sws.level()<=Byte.valueOf(value)) {
								permissions.remove(arr[2]);
							}
						} else if (express.startsWith("<")) {
							value = express.substring(1, express.length());
							if ( sws.level()<Byte.valueOf(value)) {
								permissions.remove(arr[2]);
							}
						} else if (express.startsWith(">")) {
							value = express.substring(1, express.length());
							if (sws.level()>Byte.valueOf(value)) {
								permissions.remove(arr[2]);
							}
						} else {// =
							value = express.substring(1, express.length());
							if (Byte.valueOf(value) == sws.level()) {
								permissions.remove(arr[2]);
							}
						}
					} else {
						if (subject.containsRole(arr[1])) {
							permissions.remove(arr[2]);
						}
					}
				}
			}
			if (permissions.contains("visible")) {
				ret.add(app);
			}
		}
		return ret;
	}
}
