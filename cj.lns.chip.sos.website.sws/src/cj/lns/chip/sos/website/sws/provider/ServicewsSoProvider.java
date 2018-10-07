package cj.lns.chip.sos.website.sws.provider;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.sws.so.ServicewsSO;
import cj.studio.ecm.annotation.CjService;

@CjService(name = "servicewsSoProvider")
public class ServicewsSoProvider implements ISecurityObjectProvider {

	private IAclFinder finder;
	private IAclSetting setting;
	private ServicewsSO root;

	public IAclFinder finder() {
		return finder;
	}

	@Override
	public ISoProviderHandler init(String hostId, IAclFinder finder,
			IAclSetting setting) {
		this.finder = finder;
		this.setting = setting;
		root = new ServicewsSO();
		root.setName("我的视窗");
		root.setIcon("./startMenu/img/iconfont-diannao.svg");
		return null;
	}

	public Class<?> securityObjectType() {
		return ServicewsSO.class;
	}

	public IAclSetting setting() {
		return setting;
	}

	@Override
	public String category() {
		return "system";
	}

	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext sws) {
		return root;
	}

	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		return new ArrayList<>();
	}

	@Override
	public boolean isRootVisible(Object root, ISubject subject,
			IServicewsContext ctx) {
		if (ctx.isOwner())
			return true;
		return false;
	}

	@Override
	public ServicewsSO root() {
		return root;
	}
}
