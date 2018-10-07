package cj.lns.chip.sos.website.market.app.cyberport.provider;

import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.market.app.cyberport.so.GetwaySO;
import cj.studio.ecm.annotation.CjService;

@CjService(name = "trashGatewaySoProvider")
public class TrashGatewaySoProvider
		implements ISecurityObjectProvider {
	private GetwaySO root;

	@Override
	public String category() {
		return "gateway";
	}

	@Override
	public GetwaySO root() {
		return (GetwaySO) root;
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
		return root;
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
		root = new GetwaySO();
		root.setCategory(category());
		root.setDescription("垃圾箱");
		root.setIcon("img/lajixiang.svg");
		root.setName("垃圾箱");
		root.setId("trash.gateway");
		root.setPosition(".");
		root.setProvider(hostId);
		return null;
	}


}
