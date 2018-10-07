package cj.lns.chip.sos.website.portal;

import cj.lns.chip.sos.website.framework.IRemoteDevice;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.portal.market.IDeviceMarket;
import cj.lns.chip.sos.website.portal.market.IMarketplace;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.IChip;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Frame;

@CjService(name="marketCenter")
public class MarketCenter {
	/**
	 * 返回当前视窗所在框架的市场
	 * <pre>
	 *
	 * </pre>
	 * @param frame 当前httpFrame
	 * @return
	 */
	public IMarketplace market(Frame frame){
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		IServicewsContext sws=IServicewsContext.context(frame);
		IChip c=m.subordinateChip(sws.prop("portal.id"));
		ServiceCollection<IMarketplace> col=c.site().getServices(IMarketplace.class);
		return col.isEmpty()?null:col.get(0);
	}
	/**
	 * 返回当前视窗所在框架的设备市场
	 * <pre>
	 *
	 * </pre>
	 * @param frame 当前httpFrame
	 * @return
	 */
	public IDeviceMarket device(Frame frame){
		IServiceosWebsiteModule m=ServiceosWebsiteModule.get();
		IServicewsContext sws=IServicewsContext.context(frame);
		IChip c=m.subordinateChip(sws.prop("portal.id"));
		ServiceCollection<IMarketplace> col=c.site().getServices(IMarketplace.class);
		if(col.isEmpty())return null;
		IMarketplace market=col.get(0);
		IRemoteDevice d=sws.device(frame);
		if(d.isBrowser()){
			return market.deviceMarket("browser");
		}else{
			return market.deviceMarket("mobile");
		}
	}
}
