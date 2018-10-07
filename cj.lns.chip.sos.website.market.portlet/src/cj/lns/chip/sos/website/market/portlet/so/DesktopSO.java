package cj.lns.chip.sos.website.market.portlet.so;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;
import cj.lns.chip.sos.website.ISurfacePosition;

@CjSecurityObject(resourceDefineId = ISurfacePosition.POSITION_DESKTOP, resourceDefineName = "桌面", valueIdField = "id", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visible", name = "他人可浏览")
public class DesktopSO {
	private String id;
	private String name;
	private List<String> sosPermissions;// 该应用计算后的许可列表，应用的许可有：visible,mountable
	private String provider;
	private String icon;
	private String description;


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

	public void setDescription(String description) {
		// TODO Auto-generated method stub
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
