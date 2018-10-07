package cj.lns.chip.sos.website.market.portlet.provider;

import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.market.portlet.so.DesktopSO;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;

@CjService(name = "desktopSoProvider")
public class DesktopSoProvider implements ISecurityObjectProvider {
	@CjServiceRef(refByName = "columnSoProvider")
	private ISecurityObjectProvider column;
	private DesktopSO root;
	private IAclFinder finder;
	
	@Override
	public String category() {
		return "position";
	}

	@Override
	public DesktopSO root() {
		return root;
	}
	private boolean isVisibleDesktop( IServicewsContext ctx) {
		try {
			if(ctx.isOwner())return true;
//			List<Acl> acls = finder.getAcls(ISurfacePosition.POSITION_DESKTOP, "visible", ctx);
			return finder.hasPermission(ISurfacePosition.POSITION_DESKTOP, root.getId(), "visible", ctx);
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}
	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) throws CircuitException {
		if(!isVisibleDesktop(ctx)){
			return null;
		}
		if (ISurfacePosition.POSITION_DESKTOP.equals(resourceId)) {
			return column.getChilds(resourceId, valueId, subject, ctx);
		}
		if ("column".equals(resourceId)) {
			return column.getChilds(resourceId, valueId, subject, ctx);
		}
		return null;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if(!ctx.isOwner())return null;
		if (ISurfacePosition.POSITION_DESKTOP.equals(resourceId)) {
			if (valueId.equals(root.getId())) {
				return root;
			}
			return null;
		}
		if ("column".equals(resourceId) || "portlet".equals(resourceId)) {
			return column.find(resourceId, valueId, subject, ctx);
		}
		return null;
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
		root = new DesktopSO();
		root.setDescription("左右两列");
		root.setIcon("img/column.svg");
		root.setName("桌面栏");
		root.setId("desktop.layout");
		this.finder=finder;
		return null;
	}

}
