package cj.lns.chip.sos.website.market.app.contact.component;

import cj.lns.chip.sos.cube.framework.ICube;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.market.app.contact.bo.ChatGroupBO;
import cj.lns.common.sos.website.customable.IComponent;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;

@CjService(name = "/unjoinChatGroup.service")
public class UnjoinChatGroup implements IComponent {

	@Override
	public void flow(Frame frame, Circuit circuit, IPlug plug)
			throws CircuitException {
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		IServicewsContext sws = IServicewsContext.context(frame);

		INetDisk disk = m.site().diskLnsData();
		ICube home = disk.home();
		String gid = frame.parameter("gid");

		long r = home.deleteDocOne(ChatGroupBO.KEY_COL_USER_NAME, String.format(
				"{'tuple.gid':'%s','tuple.user':'%s'}", gid, sws.owner()));
		if (r < 1) {
			throw new CircuitException("404",
					String.format("用户:%s 没有加入到该群：%s", sws.owner(), gid));
		}
	}

}
