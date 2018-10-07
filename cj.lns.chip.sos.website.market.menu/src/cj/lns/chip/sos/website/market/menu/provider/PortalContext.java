package cj.lns.chip.sos.website.market.menu.provider;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;

@CjSecurityObject(resourceDefineId = "portalx33xx", resourceDefineName = "桌面菜单", valueIdField = "id", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visible", name = "可见")
public class PortalContext {
	private String id;

	private String name;
	private String category;
	private String position = ".";// 默认在宿主内
	private List<String> sosPermissions;// 该应用计算后的许可列表，应用的许可有：visible,mountable
	private int sort;
	private String provider;
	private String icon;
	private String description;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getSosPermissions() {
		if (sosPermissions == null) {
			sosPermissions = new ArrayList<>();
		}
		return sosPermissions;
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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public void setDescription(String description) {
		// TODO Auto-generated method stub
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
