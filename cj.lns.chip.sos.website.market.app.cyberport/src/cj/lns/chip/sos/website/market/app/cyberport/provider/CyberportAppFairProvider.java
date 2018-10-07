package cj.lns.chip.sos.website.market.app.cyberport.provider;

import java.util.List;

import cj.lns.chip.sos.website.AppSO;
import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.market.app.provider.AppConfigurableProvider;
import cj.studio.ecm.annotation.CjService;

@CjService(name = "cyberportAppFairProvider")
public class CyberportAppFairProvider extends AppConfigurableProvider
		implements IAppProvider{
	public List<AppSO> getAllApps(){
		return apps();
	}
}
