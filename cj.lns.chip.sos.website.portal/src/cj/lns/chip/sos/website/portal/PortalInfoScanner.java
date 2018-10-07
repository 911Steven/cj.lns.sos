package cj.lns.chip.sos.website.portal;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.website.framework.IPortalInfoScanner;
import cj.lns.chip.sos.website.framework.info.PortalInfo;
import cj.lns.chip.sos.website.framework.info.Region;
import cj.lns.chip.sos.website.framework.info.SceneInfo;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IAssembly;
import cj.studio.ecm.IAssemblyDependency;
import cj.studio.ecm.IChip;
import cj.studio.ecm.IChipInfo;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.context.ElementGet;
import cj.studio.ecm.plugins.IPluginDependencyEvent;
import cj.ultimate.IDisposable;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.JsonArray;
import cj.ultimate.gson2.com.google.gson.JsonElement;
import cj.ultimate.gson2.com.google.gson.JsonObject;
import cj.ultimate.util.FileHelper;

@CjService(name = "portalInfoScanner",isExoteric=true)
public class PortalInfoScanner implements IPluginDependencyEvent,
		IServiceAfter, IDisposable, IPortalInfoScanner {
	String thePluginId;
	Map<String, PortalInfo> portals;
	public PortalInfoScanner() {
	}
	@Override
	public void onAfter(IServiceSite site) {
		IChip c = (IChip) site.getService(IChip.class.getName());
		thePluginId = c.info().getId();
		portals = new HashMap<String, PortalInfo>();
	}

	@Override
	public void onDependency(IAssemblyDependency root, IAssembly the,
			IAssembly parent) {
		if (thePluginId.equals(the.workbin().getProperty(
				"chip.plugin.dependencyId"))) {
			IChipInfo ci = the.workbin().chipInfo();
			try {
				String home = ci.getProperty("home.dir");
				String resource = ci.getResourceProp("resource");
				String path = String.format(
						"%s%s",
						home,
						resource.startsWith("/") ? resource : String.format(
								"/%s", resource));
				PortalInfo pi = new PortalInfo();
				fillSceneInfo(pi, path);
				fillThemes(pi, path);
				portals.put(ci.getId(), pi);
			} catch (Exception e) {
				CJSystem.current()
						.environment()
						.logging()
						.warn(String
								.format("扫描portal报错，收集此信息用于向后台报送，并不影响sos运行。portal:%s,原因：%s",
										ci.getId(), e));
			}
		}
	}

	private void fillThemes(PortalInfo pi, String path) {
		Map<String, List<String>> map = pi.getThemes();
		File dir = new File(String.format("%s/themes", path));
		File[] terminus = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		if (terminus == null || terminus.length < 1)
			return;
		for (File t : terminus) {
			List<String> themes = new ArrayList<String>();
			map.put(t.getName(), themes);
			File[] themeArr = t.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			for (File me : themeArr) {
				themes.add(me.getName());
			}
		}
	}

	private void fillSceneInfo(PortalInfo pi, String path) {
		File f = new File(path);
		if (!f.exists())
			return;
		File[] scenef = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".scene");
			}
		});
		if (scenef.length < 1) {// 必须有场景
			return;
		}
		File[] cavansf = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".canvas");
			}
		});
		
		for (File fn : scenef) {
			SceneInfo si = new SceneInfo();
			si.setSceneName(fn.getName());
			try {
				byte[] b = FileHelper.readFully(fn);
				Gson g = new Gson();
				JsonElement je = g.fromJson(new String(b), JsonElement.class);
				if (!je.isJsonObject()) {
					throw new EcmException("场景文件定义错误。" + fn.getAbsoluteFile());
				}
				JsonObject jo = (JsonObject) je;
				si.setSceneTitle(ElementGet.getJsonProp(jo.get("sceneTitle")));
				si.setSceneDesc(ElementGet.getJsonProp(jo.get("sceneDesc")));
				si.setRootRegion(ElementGet.getJsonProp(jo.get("rootRegion")));
				if (jo.get("regions") != null) {
					JsonArray ja = jo.get("regions").getAsJsonArray();
					for (JsonElement item : ja) {
						JsonObject ia = (JsonObject) item;
						fillRegions(ia, si);
					}
				}
				if (cavansf == null || cavansf.length < 1)
					continue;
				File findCanvas = null;
				String noExtName = fn.getName().substring(0,
						fn.getName().lastIndexOf(".scene"));
				for (File cs : cavansf) {
					if (cs.getName().startsWith(noExtName)) {
						findCanvas = cs;
						break;
					}
				}
				if (findCanvas != null) {
					si.setCanvasName(findCanvas.getName());
				}
				pi.getScenes().add(si);
			} catch (IOException e) {
				throw new EcmException(e);
			}
		}
	}

	private void fillRegions(JsonObject ia, SceneInfo si) {
		Region r = new Region();
		r.setName(ElementGet.getJsonProp(ia.get("name")));
		r.setPlugin(ElementGet.getJsonProp(ia.get("plugin")));
		r.setTitle(ElementGet.getJsonProp(ia.get("title")));
		si.getRegions().add(r);
	}

	public Map<String, PortalInfo> getPortals() {
		return portals;
	}

	@Override
	public void dispose() {
		portals.clear();
	}
}
