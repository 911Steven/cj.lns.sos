package cj.lns.chip.sos.website.so;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;

@CjSecurityObject(resourceDefineId = "tool", resourceDefineName = "工具", valueIdField = "id", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visible", name = "他人可浏览")
public class ToolSO implements Comparable{
	private String id;
	private String name;
	private String provider;
	private String icon;
	private String description;
	private String command;
	private int sort;
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	@Override
	public int compareTo(Object o) {
		return this.sort-((ToolSO)o).sort;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		// TODO Auto-generated method stub
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
