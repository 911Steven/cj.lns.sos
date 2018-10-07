package cj.lns.chip.sos.website.startmenu.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISecuritySite;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.startmenu.so.ToolbarSO;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjService(name = "toobarSoProvider")
public class ToolbarSoProvider implements ISecurityObjectProvider {

	public class ToolBarSoProvider implements ISoProviderHandler {

		@Override
		public void onCenterReady(ISecuritySite site) {
			tools = site.resourceProviders("tool");
		}

	}

	private ToolbarSO root;
	private IAclFinder finder;
	private Map<String, ISecurityObjectProvider> tools;// 工具的提供器

	@Override
	public String category() {
		return "position";
	}

	@Override
	public ToolbarSO root() {
		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) throws CircuitException {
		if (ISurfacePosition.POSITION_TOOLBAR.equals(resourceId)) {
			@SuppressWarnings("rawtypes")
			List list = new ArrayList<>();
			if (ctx.isOwner()) {
				for (ISecurityObjectProvider p : tools.values()) {
					list.add(p.root());
				}
			} else {
				List<Acl> acllist = finder.getAcls(resourceId, "visible", ctx);
				if (acllist.isEmpty()) {
					return list;
				}
				for (Acl acl : acllist) {
					if (acl.getAllows().length < 1) {
						return list;
					}
				}
				acllist = finder.getAcls("tool", "visible", ctx);
				Map<String, Object> map = new HashMap<>();
				for (ISecurityObjectProvider p : tools.values()) {
					ISecurityObject so = ISecurityObject
							.securityObject(p.root());
					map.put(so.valueId(), p.root());
				}
				for (Acl acl : acllist) {
					if (acl.getAllows().length < 1) {
						continue;
					}

					if (subject.containsRole("guestUsers")) {
						if (acl.isAllowSubject("role", "publicToNet")) {
							if (map.containsKey(acl.getValueId()))
								list.add(map.get(acl.getValueId()));
						}
						continue;
					} else {
						if (acl.isAllowSubject("role", "publicToMember")) {
							if (map.containsKey(acl.getValueId()))
								list.add(map.get(acl.getValueId()));
							continue;
						} else {
							if (map.containsKey(acl.getValueId())) {
								list.add(map.get(acl.getValueId()));
							}
							continue;
						}
					}

				}

			}
			Collections.sort(list);
			return list;
		}
		if ("tool".equals(resourceId)) {
			ISecurityObjectProvider p = tools.get(valueId);
			if (p == null)
				return null;
			return p.getChilds(resourceId, valueId, subject, ctx);
		}
		return null;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if (ISurfacePosition.POSITION_TOOLBAR.equals(resourceId)) {
			if (valueId.equals(root.getId())) {
				return root;
			}
			return null;
		}
		if ("tool".equals(resourceId)) {
			if (!tools.containsKey(valueId)) {
				return null;
			}
			ISecurityObjectProvider p = tools.get(valueId);
			return p.find(resourceId, valueId, subject, ctx);
		}
		ISecurityObjectProvider p = null;
		/*
		 * 由于工具栏上放的是应用、联系人资源，因此：
		 * 发布、应用属于应用资源、联系人属于联系人资源,退出属于系统命令资源
		 */
		if ("application".equals(resourceId)) {
			p = tools.get(ISurfacePosition.POSITION_TOOLBAR_WIN_APP);
			return p.find(resourceId, valueId, subject, ctx);
		}
		if ("publish".equals(resourceId)) {
			p = tools.get(ISurfacePosition.POSITION_TOOLBAR_WIN_PUBLISH);
			return p.find(resourceId, valueId, subject, ctx);
		}
		if ("contact".equals(resourceId)) {
			p = tools.get(ISurfacePosition.POSITION_TOOLBAR_WIN_CONTACT);
			return p.find(resourceId, valueId, subject, ctx);
		}
		if ("systemcmd".equals(resourceId)) {
			p = tools.get(ISurfacePosition.POSITION_TOOLBAR_WIN_SWITCH);
			return p.find(resourceId, valueId, subject, ctx);
		}
		return null;
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		if (ctx.isOwner())
			return true;
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder,
			IAclSetting setting) {
		root = new ToolbarSO();
		root.setDescription("工具栏");
		root.setIcon("img/toolbar.svg");
		root.setName("工具栏");
		root.setId("toolbar");
		this.finder = finder;
		// 到安全中心注入各个Tool提供器
		return new ToolBarSoProvider();
	}

}
