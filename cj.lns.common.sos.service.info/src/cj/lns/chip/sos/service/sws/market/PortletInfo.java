package cj.lns.chip.sos.service.sws.market;

import java.util.Date;

import cj.lns.chip.sos.service.PluginInfo;



public class PortletInfo{
	PluginInfo plugin;
	boolean isUsed;
	Date createTime;
	String owner;
	public PluginInfo getPlugin() {
		return plugin;
	}
	public void setPlugin(PluginInfo plugin) {
		this.plugin = plugin;
	}
	public boolean isUsed() {
		return isUsed;
	}
	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
}
