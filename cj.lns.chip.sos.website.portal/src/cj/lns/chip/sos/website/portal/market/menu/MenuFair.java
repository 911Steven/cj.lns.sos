package cj.lns.chip.sos.website.portal.market.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.IMenuProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.logging.ILogging;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;
import cj.ultimate.util.StringUtil;

public class MenuFair implements IMenuFair {
	private Map<String, IMenuCategory> category;// key:分类标识
	ILogging log;

	public MenuFair() {
		category = new HashMap<>();
		log = CJSystem.current().environment().logging();
	}

	public IMenuCategory category(String category) {
		return this.category.get(category);
	}

	public boolean containsCategory(String category) {
		return this.category.containsKey(category);
	}

	public String[] enumCategory() {
		return category.keySet().toArray(new String[0]);
	}

	@Override
	public void loadCategoryConfig(IServiceosWebsiteModule m) throws CircuitException {
		byte[] b = m.context().resource("/fair.category.json");
		JsonElement e = new Gson().fromJson(new String(b), JsonElement.class);
		JsonArray ja = e.getAsJsonArray();
		for (JsonElement je : ja) {
			JsonObject jo = je.getAsJsonObject();
			String cid = ElementGet.getJsonProp(jo.get("categoryId"));
			if (StringUtil.isEmpty(cid)) {
				log.warn(String.format(
						"模块%s中集市分类定义：fair.category.json中有未确定的分类id定义",
						m.chip().info().getId()));
				continue;
			}
			String cname = ElementGet.getJsonProp(jo.get("categoryName"));
			String cdesc = ElementGet.getJsonProp(jo.get("desc"));
//			String cIcon = ElementGet.getJsonProp(jo.get("categoryIcon"));
			IMenuCategory c = null;
			if (!category.containsKey(cid)) {
				c = new MenuCategory();
				category.put(cid, c);
			} else {
				c = category.get(cid);
			}
			c.setCategoryName(cname);
			c.setCategoryDesc(cdesc);
//			c.setCategoryIcon(cIcon);
		}

	}

	@Override
	public void addMenuProviders(String moduleName,IServiceosWebsiteModule m,
			List<IMenuProvider> providers) {
		for (IMenuProvider p : providers) {
			p.init(m);
			String cname = p.category();
			if (StringUtil.isEmpty(cname)) {
				log.warn(String.format("模块%s中的提供器类别为空", moduleName));
			}
			IMenuCategory c = null;
			if (!category.containsKey(cname)) {
				log.warn(String.format("不支持模块:%s中的提供器分类:%s，因此提供器无效", moduleName,
						cname));
				continue;
			} else {
				c = category.get(cname);
			}
			c.addMenuProvider(moduleName,p);
		}
	}
}
