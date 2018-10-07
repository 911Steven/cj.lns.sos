package cj.lns.chip.sos.website.sws.so;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;
import cj.lns.chip.sos.website.AppSO;

@CjSecurityObject(resourceDefineId = "publish", resourceDefineName = "发布快捷方式", valueIdField = "phyId", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visible", name = "谁能看发布按钮")
public class PublishSO extends AppSO{
	public void fill(AppSO so){
		this.setCommand(so.getCommand());
		this.setDesc(so.getDesc());
		this.setIcon(so.getIcon());
		this.setId(so.getId());
		this.setName(so.getName());
		this.setProvider(so.getProvider());
		this.setCategory(so.getCategory());
		this.setPosition(so.getPosition());
		this.setTarget(so.getTarget());
		this.setPhyId(so.getPhyId());
	}
	
}
