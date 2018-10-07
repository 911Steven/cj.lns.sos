package cj.lns.chip.sos.website.sws.so;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;

@CjSecurityObject(resourceDefineId = "servicews", resourceDefineName = "视窗", valueIdField = "swsObjectInst", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visit", name = "谁能访问我的视窗")
@CjPermission(code = "enterport", name = "我能看谁的信息")
@CjPermission(code = "leaveport", name = "谁能看我的信息")
public class ServicewsSO {
	String swsid;
	String swsObjectInst="the.servicews";//视窗资源对象实例标识，此值为约定不变的。
	String name;
	String icon;
	public String getSwsObjectInst() {
		return swsObjectInst;
	}
	public String getIcon() {
		return icon;
	}
	public String getName() {
		return name;
	}
	public String getSwsid() {
		return swsid;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSwsid(String swsid) {
		this.swsid = swsid;
	}
}
