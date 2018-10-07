package cj.lns.chip.sos.website.market.app.home.provider;

import cj.lns.chip.sos.sws.security.annotation.CjPermission;
import cj.lns.chip.sos.sws.security.annotation.CjSecurityObject;
import cj.lns.chip.sos.website.AppSO;

@CjSecurityObject(resourceDefineId = "home", resourceDefineName = "站点", valueIdField = "phyId", valueNameField = "name", valueIconField = "icon")
@CjPermission(code = "visible", name = "可见")
public class HomeSO extends AppSO{
	
}
