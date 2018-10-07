package cj.lns.chip.sos.website.framework.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalInfo {
	List<SceneInfo> scenes;
	Map<String, List<String>> themes;//key=终端类型，value=主题目录名,主题也是按终端类型分类
	List<String> menus;//pluginId
	List<String> apps;//pluginId
	List<String> lets;//pluginId
	List<String> popups;//pluginId
	public PortalInfo() {
		scenes=new ArrayList<SceneInfo>();
		themes=new HashMap<String, List<String>>();
		menus=new ArrayList<String>();
		apps=new ArrayList<String>();
		lets=new ArrayList<String>();
		popups=new ArrayList<String>();
	}
	public List<String> getPopups() {
		return popups;
	}
	public List<String> getApps() {
		return apps;
	}
	public List<String> getLets() {
		return lets;
	}
	public List<String> getMenus() {
		return menus;
	}
	public List<SceneInfo> getScenes() {
		return scenes;
	}
	public Map<String,List< String>> getThemes() {
		return themes;
	}
}
