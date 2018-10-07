package cj.lns.chip.sos.service.portal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.service.PluginInfo;

public class PortalInfo {
	PluginInfo info;
	//一个框架会有一个视窗模板，在用户关注此框架时，以此模板为用户默认实例化一个视窗
	BigInteger swsTemplateId;
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
	public PluginInfo getInfo() {
		return info;
	}
	public void setInfo(PluginInfo info) {
		this.info = info;
	}
	public BigInteger getSwsTemplateId() {
		return swsTemplateId;
	}
	public void setSwsTemplateId(BigInteger swsTemplateId) {
		this.swsTemplateId = swsTemplateId;
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
	public void setApps(List<String> apps) {
		this.apps = apps;
	}
	public void setLets(List<String> lets) {
		this.lets = lets;
	}
	public void setMenus(List<String> menus) {
		this.menus = menus;
	}
	public void setPopups(List<String> popups) {
		this.popups = popups;
	}
}
