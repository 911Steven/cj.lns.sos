package cj.lns.chip.sos.website.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISecuritySite;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IChip;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.logging.ILogging;
import cj.ultimate.IDisposable;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjService(name = "securityCenter")
public class SecurityCenter
		implements ISecurityCenter, IDisposable {

	public class SecuritySite implements ISecuritySite {

		@Override
		public Map<String, ISecurityObjectProvider> resourceProviders(
				String resourceId) {
			if (!resources.containsKey(resourceId)) {
				return new HashMap<>();
			}
			ISecurityResource sr = resources.get(resourceId);
			Map<String, ISecurityObjectProvider> map = new HashMap<>();
			String[] ids = sr.enumResourceImpl();
			for (String id : ids) {
				SecurityResourceImpl impl = (SecurityResourceImpl) sr
						.resourceImpl(id);
				map.put(id, impl.provider);
			}
			return map;
		}

	}

	Map<String, ISecurityResource> resources;// key:资源id，即提供器所定义的id，如果冲突则报异常
	Map<String, Category> categories;// key:类别，V：资源
	ILogging log;
	@CjServiceRef(refByName = "paService")
	IPaService pa;
	List<ISoProviderHandler> handlers;

	@Override
	public void dispose() {
		resources.clear();
		categories.clear();
		handlers.clear();
	}

	public SecurityCenter() {
		resources = new HashMap<>();
		categories = new HashMap<>();
		handlers = new ArrayList<>();
	}

	public void init() {
		log = CJSystem.current().environment().logging();
		try {
			IServiceosWebsiteModule security = ServiceosWebsiteModule.get();
			byte[] b = security.context().resource("/security.center.json");
			List<Category> list = new Gson().fromJson(new String(b),
					new TypeToken<List<Category>>() {
					}.getType());
			for (Category c : list) {
				if (categories.containsKey(c.categoryId)) {
					throw new EcmException("有重名的安全分类定义");
				}
				categories.put(c.categoryId, c);
			}
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}

	@Override
	public Category category(String id) {
		return categories.get(id);
	}

	@Override
	public String[] enumCategory() {
		Category[] set = categories.values().toArray(new Category[0]);
		Arrays.sort(set, new Comparator<Category>() {
			@Override
			public int compare(Category o1, Category o2) {
				return o1.sort - o2.sort;
			}
		});
		String[] arr = new String[set.length];
		for (int i = 0; i < set.length; i++) {
			arr[i] = set[i].categoryId;
		}
		return arr;
	}

	@Override
	public void scans(IServiceosWebsiteModule m) {

		ServiceCollection<ISecurityObjectProvider> col = m.chip().site()
				.getServices(ISecurityObjectProvider.class);
		if (!col.isEmpty()) {
			loadProvider(m.chip().info().getId(), col);
		}
		String ids[] = m.enumSubordinateChipId();
		for (String name : ids) {
			IChip c = m.subordinateChip(name);
			IServiceosWebsiteModule sm = (IServiceosWebsiteModule) c.site()
					.getService("serviceosWebsiteModule");
			if (sm != null) {
				scans(sm);
			}
		}
		ISecuritySite site = new SecuritySite();
		for (ISoProviderHandler h : handlers) {
			h.onCenterReady(site);
		}
	}

	protected void loadProvider(String hostId,
			ServiceCollection<ISecurityObjectProvider> col) {
		for (ISecurityObjectProvider p : col) {
			String category = p.category();
			if (StringUtil.isEmpty(category)
					|| !categories.containsKey(category)) {
				throw new EcmException(
						String.format("提供器欲插入的安全类别:%s不存在：%s", category, p));
			}
			IAclFinder finder = new AclFinder(this, p);
			IAclSetting setting = new AclSetting(this, p, pa);
			ISoProviderHandler handler = p.init(hostId, finder, setting);// valueId为空时表示动态资源，则不被索引
			if (handler != null) {
				handlers.add(handler);
			}
			Object r = p.root();
			if (r == null) {
				log.warn(String.format(
						"无效。模块%s中有安全提供器未指定根对象，必须使方法返回值：root() :%s", hostId, p));
				continue;
			}
			ISecurityObject so = ISecurityObject.securityObject(r);
			if (StringUtil.isEmpty(so.valueId())) {
				log.warn(String.format("模块%s中有安全提供器未定义资源实例(valueId为空)，因此无效:%s",
						hostId, p));
				continue;
			}
			if (StringUtil.isEmpty(so.resourceId())) {
				log.warn(String.format("无效。模块%s中的安全对象提供器定义的资源id为空:%s", hostId,
						p));
				continue;
			}
			if (StringUtil.isEmpty(so.resourceName())) {
				log.warn(String.format("无效。模块%s中的安全对象提供器定义的资源名为空:%s", hostId,
						p));
				continue;
			}
			if (so.permissions().isEmpty()) {
				log.warn(String.format("模块%s中的安全对象提供器未定义任何许可:%s,但仍然可使用", hostId,
						p));
				// continue;
			}
			ISecurityResource sr = (SecurityResource) resources
					.get(so.resourceId());
			ISecurityResourceImpl impl = null;
			if (sr == null) {
				sr = new SecurityResource();
				resources.put(so.resourceId(), sr);
				impl = new SecurityResourceImpl(hostId, so, p, finder, setting);
				sr.add(so.valueId(), impl);
			} else {
				if (sr.containsResourceImpl(so.valueId())) {
					impl = ((SecurityResource) sr).fixed.get(so.valueId());
				} else {
					impl = new SecurityResourceImpl(hostId, so, p, finder,
							setting);
					sr.add(so.valueId(), impl);
				}
			}
			Category c = categories.get(category);
			if (c.resourceImpls.contains(impl)) {
				log.warn(String.format(
						"无效。模块\"%s\"中的安全对象提供器 %s 已在分类\"%s\"中存在，冲突的原因：valueName=%s;valueId=%s,冲突的提供器：valueName=%s;valueId=%s 在模块 %s",
						hostId, p, c.categoryName, so.valueName(), so.valueId(),
						impl.valueName(), impl.valueId(), impl.getHostId()));
				continue;
			}
			c.resourceImpls.add(impl);
		}

	}

	@Override
	public String[] enumSecurityResource() {
		return resources.keySet().toArray(new String[0]);
	}

	@Override
	public ISecurityResource resource(String resourceName) {
		SecurityResource sr = (SecurityResource) resources.get(resourceName);
		return sr;
	}

	@Override
	public boolean containsResource(String resourceName) {
		return resources.containsKey(resourceName);
	}

}
