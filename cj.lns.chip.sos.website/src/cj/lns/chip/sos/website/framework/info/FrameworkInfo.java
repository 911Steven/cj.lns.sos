package cj.lns.chip.sos.website.framework.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示sos.website的框架信息
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
public class FrameworkInfo {
	String websiteId;
	Map<String,PortalInfo>portals;
	List<PluginInfo> plugins;
	
	public FrameworkInfo() {
		plugins=new ArrayList<PluginInfo>();
		portals=new HashMap<String, PortalInfo>();
	}
	public List<PluginInfo> getPlugins() {
		return plugins;
	}
	public Map<String, PortalInfo> getPortals() {
		return portals;
	}
	public String getWebsiteId() {
		return websiteId;
	}
	public void setWebsiteId(String websiteId) {
		this.websiteId = websiteId;
	}
}
