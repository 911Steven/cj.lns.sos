package cj.lns.chip.sos.website.portal.market;

import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.website.IAppProvider;
import cj.lns.chip.sos.website.IMenuProvider;
import cj.lns.chip.sos.website.IPortletProvider;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.portal.market.app.IAppFair;
import cj.lns.chip.sos.website.portal.market.menu.IMenuFair;
import cj.lns.chip.sos.website.portal.market.portlet.IPortletFair;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.IChip;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.graph.CircuitException;

public abstract class Marketplace implements IMarketplace {
	private Map<String, IDeviceMarket> deviceMarkets;// key:browser,mobile

	public Marketplace() {
		deviceMarkets = new HashMap<>();
	}

	public boolean contains(String device) {
		return deviceMarkets.containsKey(device);
	}

	public IDeviceMarket deviceMarket(String device) {
		return deviceMarkets.get(device);
	}

	public String[] enumDeviceMarket() {
		return deviceMarkets.keySet().toArray(new String[0]);
	}

	@Override
	public final void scans(IServiceosWebsiteModule m) {
		deviceMarkets.clear();
		String[] deviceids = m.enumSubordinateChipId();
		for (String name : deviceids) {
			IChip device = m.subordinateChip(name);
			IDeviceMarket dm = new DeviceMarket();
			IServiceosWebsiteModule devicem = (IServiceosWebsiteModule) device
					.site().getService("serviceosWebsiteModule");
			if (devicem == null)
				continue;
			scansDevice(devicem, dm);
			this.deviceMarkets.put(name, dm);
		}
	}

	private void scansDevice(IServiceosWebsiteModule device, IDeviceMarket dm) {
		String[] ids = device.enumSubordinateChipId();
		for (String name : ids) {
			IChip chip = device.subordinateChip(name);
			IServiceosWebsiteModule chipm = (IServiceosWebsiteModule) chip
					.site().getService("serviceosWebsiteModule");
			if (chipm == null)
				continue;
			if ("application".equals(name)) {
				IAppFair fair = dm.getAppFair();
				try {
					fair.loadCategoryConfig(chipm);
					scansAppProvider(fair, chipm);
				} catch (CircuitException e) {
					if ("404".equals(e.getStatus())) {
						CJSystem.current().environment().logging()
								.warn(String.format(
										"模块%s中未有集市分类定义：fair.category.json，因此该模块不支持市场。",
										chipm.chip().info().getId()));
					}
				}
				continue;
			}
			if ("menu".equals(name)) {
				IMenuFair fair = dm.getMenuFair();
				try {
					fair.loadCategoryConfig(chipm);
					scansMenuProvider(fair, chipm);
				} catch (CircuitException e) {
					if ("404".equals(e.getStatus())) {
						CJSystem.current().environment().logging()
								.warn(String.format(
										"模块%s中未有集市分类定义文件：fair.category.json，因此该模块不支持市场。",
										chipm.chip().info().getId()));
					}
				}
				continue;
			}
			if ("portlets".equals(name)) {
				IPortletFair fair = dm.getPortletFair();
				try {
					fair.loadCategoryConfig(chipm);
					scansPortletProvider(fair, chipm);
				} catch (CircuitException e) {
					if ("404".equals(e.getStatus())) {
						CJSystem.current().environment().logging()
								.warn(String.format(
										"模块%s中未有集市分类定义：fair.category.json，因此该模块不支持市场。",
										chipm.chip().info().getId()));
					}
				}
				continue;
			}
		}
	}

	protected void scansPortletProvider(IPortletFair fair,
			IServiceosWebsiteModule portletModule) {
		String[] ids = portletModule.enumSubordinateChipId();
		for (String name : ids) {
			IChip chip = portletModule.subordinateChip(name);
			ServiceCollection<IPortletProvider> providers = chip.site()
					.getServices(IPortletProvider.class);
			if (providers.isEmpty())
				continue;
			IServiceosWebsiteModule cm=(IServiceosWebsiteModule)chip.site().getService("serviceosWebsiteModule");
			fair.addPortletProviders(name,cm, providers.asList());
		}
	}

	protected void scansMenuProvider(IMenuFair fair,
			IServiceosWebsiteModule menuModule) {
		String[] ids = menuModule.enumSubordinateChipId();
		for (String name : ids) {
			IChip chip = menuModule.subordinateChip(name);
			ServiceCollection<IMenuProvider> providers = chip.site()
					.getServices(IMenuProvider.class);
			if (providers.isEmpty())
				continue;
			IServiceosWebsiteModule cm=(IServiceosWebsiteModule)chip.site().getService("serviceosWebsiteModule");
			fair.addMenuProviders(name,cm, providers.asList());
		}
	}

	protected void scansAppProvider(IAppFair fair,
			IServiceosWebsiteModule appModule) {
		String[] ids = appModule.enumSubordinateChipId();
		for (String name : ids) {
			IChip chip = appModule.subordinateChip(name);
			ServiceCollection<IAppProvider> providers = chip.site()
					.getServices(IAppProvider.class);
			if (providers.isEmpty())
				continue;
			IServiceosWebsiteModule cm=(IServiceosWebsiteModule)chip.site().getService("serviceosWebsiteModule");
			fair.addAppProviders(name,cm, providers.asList());
		}

	}

}
