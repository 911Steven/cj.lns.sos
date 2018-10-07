package cj.lns.chip.sos.website.portal;

import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.portal.market.IMarketplace;
import cj.lns.common.sos.website.customable.Program;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IChip;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.plugins.moduleable.IModuleEntrypoint;

@CjService(name = "moduleEntrypoint")
public class ModuleEntrypoint extends Program implements IModuleEntrypoint {
	@Override
	protected void program(IModule m) {
		scansPortalMarkets(m);
	}

	public void scansPortalMarkets(IModule m) {

		String[] ids = m.enumSubordinateChipId();
		for (String name : ids) {
			IChip c = m.subordinateChip(name);
			ServiceCollection<IMarketplace> markets = c.site()
					.getServices(IMarketplace.class);
			if (markets.isEmpty()) {
				throw new EcmException(String.format("框架:%s中缺少市场服务", name));
			}
			IMarketplace market = markets.get(0);
			IServiceosWebsiteModule portal = (IServiceosWebsiteModule) c.site()
					.getService("serviceosWebsiteModule");
			if (portal != null) {
				market.scans(portal);
				CJSystem.current().environment().logging().info(getClass(),String.format("框架:%s的市场已初始化", name));
			}
		}
	}
}
