package cj.lns.chip.sos.website.market.menu.provider;

import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.annotation.CjService;

@CjService(name = "desktopAppSecurityObjectProvider")
public class DesktopMenuSecurityObjectProvider
		implements ISecurityObjectProvider {
	private PortalContext root;

	@Override
	public String category() {
		return "position";
	}

	@Override
	public PortalContext root() {
		return (PortalContext) root;
	}

	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		return null;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if(valueId.equals(root.getId())){
			return root;
		}
		return false;
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		if (ctx.isOwner())
			return true;
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder, IAclSetting setting) {
		// TODO Auto-generated method stub
		root = new PortalContext();
		root.setCategory(category());
		root.setDescription("菜单按位置列出");
		root.setIcon("img/menu.svg");
		root.setName("菜单栏");
		root.setId(ISurfacePosition.POSITION_TOOLBAR);
		root.setPosition(".");
		root.setProvider(hostId);
		return null;
	}


}
