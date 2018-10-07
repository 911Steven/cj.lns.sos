package cj.lns.chip.sos.website.market.app.contact.so;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;

@CjSecurityObject(resourceDefineId = "contact", resourceDefineName = "联系人", valueIdField = "id", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visible", name = "可见")
public class ContactSO implements Comparable<ContactSO>{
	private String id;
	private String command;
	private String provider;
	private String icon;
	private String position=".";//默认在宿主内
	private String name;
	private String desc;
	private int sort;
	@Override
	public int compareTo(ContactSO o) {
		return sort-o.sort;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
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
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
