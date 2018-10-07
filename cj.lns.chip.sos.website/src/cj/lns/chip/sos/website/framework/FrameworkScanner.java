package cj.lns.chip.sos.website.framework;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.framework.info.FrameworkInfo;
import cj.lns.chip.sos.website.framework.info.PluginInfo;
import cj.lns.chip.sos.website.framework.info.PortalInfo;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IAssembly;
import cj.studio.ecm.IChipInfo;
import cj.studio.ecm.IWorkbin;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.plugins.IAssemblyPlugins;
import cj.ultimate.util.FileHelper;

/**
 * 扫描框架配置，报告给数据库
 * 
 * <pre>
 *
 * </pre>
 * 
 * @author carocean
 *
 */
@CjService(name = "frameworkScanner")
public class FrameworkScanner {
	public FrameworkScanner() {
	}

	public FrameworkInfo scan(IAssemblyPlugins root) {
		FrameworkInfo framework = new FrameworkInfo();
		framework.setWebsiteId(root.rootAssemblyId());
		List<PluginInfo> plugins = framework.getPlugins();
		IAssembly pass = root.find("portal");
		ServiceCollection<IPortalInfoScanner> col = pass.workbin().part(
				IPortalInfoScanner.class);
		if (col.isEmpty()) {
			throw new EcmException("portal芯片缺少portal扫描器");
		}
		IPortalInfoScanner scanner = col.get(0);
		Map<String, PortalInfo> portals = framework.getPortals();
		if (!scanner.getPortals().isEmpty()) {
			portals.putAll(scanner.getPortals());
		}
		IChipInfo ci = root.chipInfo();
		PluginInfo sos = newPluginInfo(ci);
		sos.setDependencyPlugin("-");// -表示为没有依赖，为根插件
		plugins.add(sos);
		scanChild(framework.getWebsiteId(), plugins, portals, root);

		return framework;
	}

	private void scanChild(String the, List<PluginInfo> plugins,
			Map<String, PortalInfo> portals, IAssemblyPlugins root) {
		List<IAssembly> childs = root.findChilds(the);
		for (IAssembly a : childs) {
			IWorkbin bin = a.workbin();
			IChipInfo ci = bin.chipInfo();
			String parentId = bin.getProperty("chip.plugin.dependencyId");
			if ("popup".equals(parentId)) {
				IAssembly popup = root.find(parentId);
				String portalid = popup.workbin().getProperty(
						"chip.plugin.dependencyId");
				PortalInfo pi = portals.get(portalid);
				if (pi != null) {
					pi.getPopups().add(ci.getId());
				}
			} else if ("app".equals(parentId)) {
				IAssembly let = root.find(parentId);
				String portalid = let.workbin().getProperty(
						"chip.plugin.dependencyId");
				PortalInfo pi = portals.get(portalid);
				if (pi != null) {
					pi.getApps().add(ci.getId());
				}
			} else if ("portlet".equals(parentId)) {
				IAssembly let = root.find(parentId);
				String portalid = let.workbin().getProperty(
						"chip.plugin.dependencyId");
				PortalInfo pi = portals.get(portalid);
				if (pi != null) {
					pi.getLets().add(ci.getId());
				}
			} else if ("menu".equals(parentId)) {
				IAssembly let = root.find(parentId);
				String portalid = let.workbin().getProperty(
						"chip.plugin.dependencyId");
				PortalInfo pi = portals.get(portalid);
				if (pi != null) {
					pi.getMenus().add(ci.getId());
				}
			}
			PluginInfo pi = newPluginInfo(ci);
			scanChild(ci.getId(), plugins, portals, root);
			pi.setDependencyPlugin(the);
			plugins.add(pi);
		}
	}

	private PluginInfo newPluginInfo(IChipInfo ci) {
		PluginInfo pi = new PluginInfo();
		pi.setAssemblyGuid(ci.getId());
		pi.setAssemblyDescription(ci.getDescription());
		pi.setAssemblyTitle(ci.getName());
		pi.setAssemblyVersion(ci.getVersion());
		pi.setAssemblyIcon(ci.getIconFileName());
		pi.setAssemblyCompany(ci.getCompany());
		pi.setAssemblyCopyright(ci.getCopyright());
		pi.setAssemblyProduct(ci.getProduct());
		pi.setAssemblyDeveloperHome(ci.getDeveloperHome());
		if (ci.getIconStream() != null) {
			byte[] b;
			try {
				b = FileHelper.readFully(ci.getIconStream());
				pi.setAssemblyIconBytes(b);
			} catch (IOException e) {
			}
		}else{//系统给个默认图标
			
		}
		return pi;
	}

}
