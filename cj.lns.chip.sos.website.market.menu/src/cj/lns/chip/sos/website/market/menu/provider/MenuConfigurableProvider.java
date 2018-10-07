package cj.lns.chip.sos.website.market.menu.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.IMenuProvider;
import cj.lns.chip.sos.website.MenuSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;

/**
 * 可配置的栏目提供器
 * 
 * <pre>
 * ~ 必须声明为服务
 * ~ 支持配置文件
 * ~ 在服务初始化后加载配置文件
 * ~ 在栏目程序的Modulepoint中装载该提供器
 * </pre>
 * 
 * @author carocean
 *
 */
public abstract class MenuConfigurableProvider implements IMenuProvider {
	private String categoryId;
	private List<MenuSO> menus;
	private Map<String, String[]> acl;

	public void init(IServiceosWebsiteModule m) {
		try {
			byte[] b = m.context().resource(getConfigJsonFile());
			String json = new String(b);
			JsonElement je = new Gson().fromJson(json, JsonElement.class);
			JsonObject jo = je.getAsJsonObject();
			categoryId = ElementGet.getJsonProp(jo.get("category"));
			acl = new HashMap<>();
			JsonArray ja = jo.get("menus").getAsJsonArray();
			menus = new ArrayList<>();
			for (JsonElement jemenu : ja) {
				JsonObject jomenu = jemenu.getAsJsonObject();
				MenuSO menu = new Gson().fromJson(jomenu, MenuSO.class);
				menu.setProvider(m.chip().info().getId());
				menu.setCategory(categoryId);
				menus.add(menu);
				List<String> aclList = new ArrayList<>();
				JsonElement jeacl = jomenu.get("acl");
				if (jeacl == null) {
					continue;
				}
				JsonArray jaacl = jeacl.getAsJsonArray();
				for (JsonElement jacl : jaacl) {
					String item = jacl.getAsString();
					String[] arr = item.split(" ");
					if (arr.length != 3) {
						throw new CircuitException("503", String
								.format("栏目%s的ACL项配置错误:%s", menu.getId(), item));
					}
					aclList.add(item);
				}
				
				acl.put(menu.getId(), aclList.toArray(new String[0]));
			}
		} catch (CircuitException e) {
			throw new EcmException(String.format("装载栏目提供器失败，原因：%s %s",
					e.getStatus(), e.getMessage()));
		}
	}

	/**
	 * 配置文件
	 * 
	 * <pre>
	 * 默认文件在 resources/menu.fair.plugin.json
	 * </pre>
	 * 
	 * @return
	 */
	protected String getConfigJsonFile() {
		return "/menu.fair.plugin.json";
	}

	@Override
	public String category() {
		return categoryId;
	}
	@Override
	public MenuSO findMenu(ISubject subject, String menuId) {
		MenuSO found = null;
		for (MenuSO a : menus) {
			if (menuId.equals(a.getId())) {
				found = a;
				break;
			}
		}
		if (found == null) {
			return null;
		}
		String[] aclArr = acl.get(menuId);
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
	public List<MenuSO> getMenus(ISubject subject) {
		List<MenuSO> ret = new ArrayList<>();
		for (MenuSO menu : menus) {
			if (!this.acl.containsKey(menu.getId()))
				continue;
			String[] acl = this.acl.get(menu.getId());
			List<String> permissions = menu.getSosPermissions();
			for (String ace : acl) {
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
				ret.add(menu);
			}
		}
		return ret;
	}
}
