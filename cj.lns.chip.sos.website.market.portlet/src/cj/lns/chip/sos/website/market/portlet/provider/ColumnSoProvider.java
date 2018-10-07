package cj.lns.chip.sos.website.market.portlet.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.market.portlet.so.ColumnSO;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IChip;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjService(name = "columnSoProvider")
public class ColumnSoProvider implements ISecurityObjectProvider {

	private Map<String, Object> columns;
	private ColumnSO root;
	private List<ISecurityObjectProvider> providers;// 栏目提供器
	private IAclFinder finder;

	@Override
	public String category() {
		return "portlet";
	}

	@Override
	public ColumnSO root() {
		return root;
	}
	private boolean isVisibleColumn(String resourceId,String valueId,  IServicewsContext ctx) {
		try {
			if(ctx.isOwner())return true;
			return finder.hasPermission(resourceId, valueId, "visible", ctx);
		} catch (CircuitException e) {
			throw new EcmException(e);
		}
	}
	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) throws CircuitException {
		if (ISurfacePosition.POSITION_DESKTOP.equals(resourceId)) {
			List<ColumnSO> list = new ArrayList<>();
			for (Object o : columns.values()) {
				if (o instanceof ColumnSO) {
					list.add((ColumnSO) o);
				}
			}
			return list;
		}
		if("column".equals(resourceId)){
			if(!isVisibleColumn(resourceId,valueId,  ctx)){
				return null;
			}
			List<Object> list = new ArrayList<>();
			for (ISecurityObjectProvider p : providers) {
				List<?> set = p.getChilds(resourceId, valueId, subject, ctx);
				if (set == null || set.isEmpty())
					continue;
				for(Object o:set){
					PortletSO so=(PortletSO)o;
					if(valueId.equals(so.getPosition())){
						list.add(so);
					}
				}
			}
			return list;
		}
		
		return null;
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if(!ctx.isOwner())return null;
		if ("column".equals(resourceId)) {
			return columns.get(valueId);
		}
		if ("portlet".equals(resourceId)) {
			for (ISecurityObjectProvider p : providers) {
				Object o = p.find(resourceId, valueId, subject, ctx);
				if (o == null)
					return o;
				PortletSO let = (PortletSO) o;
				if (valueId.equals(let.getPhyId().toString())) {
					return let;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		// if (ctx.isOwner())
		// return true;
		return false;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder, IAclSetting setting) {
		// TODO Auto-generated method stub
		this.finder=finder;
		root = new ColumnSO();
		root.setDescription("列");
		root.setIcon("./servicews/img/head.jpg");
		root.setName("列");
		root.setId("column");
		root.setPosition(".");
		root.setProvider(hostId);

		ColumnSO col = new ColumnSO();
		col.setDescription("左列");
		col.setIcon("img/column-set.svg");
		col.setName("左列");
		col.setId(ISurfacePosition.POSITION_DESKTOP_LEFT);
		col.setPosition(ISurfacePosition.POSITION_DESKTOP_LEFT);

		ColumnSO col2 = new ColumnSO();
		col2.setDescription("右列");
		col2.setIcon("img/column-set.svg");
		col2.setName("右列");
		col2.setId(ISurfacePosition.POSITION_DESKTOP_RIGHT);
		col2.setPosition(ISurfacePosition.POSITION_DESKTOP_RIGHT);
		columns = new HashMap<>();
		columns.put(col.getId(), col);
		columns.put(col2.getId(), col2);
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		String[] ids = m.enumSubordinateChipId();
		providers = new ArrayList<>();
		for (String id : ids) {
			IChip c = m.subordinateChip(id);
			ServiceCollection<ISecurityObjectProvider> sp = c.site()
					.getServices(ISecurityObjectProvider.class);
			providers.addAll(sp.asList());
		}
		return null;
	}

}
