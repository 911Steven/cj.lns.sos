package cj.lns.chip.sos.website.market.app.contact.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.sws.security.ISoProviderHandler;
import cj.lns.chip.sos.website.ISurfacePosition;
import cj.lns.chip.sos.website.framework.IServiceosWebsiteModule;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.lns.chip.sos.website.market.app.contact.so.ContactSO;
import cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjService(name = "contactSoProvider")
public class ContactSoProvider implements ISecurityObjectProvider {
	private ContactSO root;
	private HashMap<String, ContactSO> myProperties;

	@Override
	public String category() {
		return "application";
	}

	@Override
	public ContactSO root() {
		return (ContactSO) root;
	}

	// 不同的视窗不同的主页模板
	@Override
	public List<?> getChilds(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		if("contact".equals(resourceId)&&valueId.equals(root.getId())){
			List<ContactSO> list=new ArrayList<>();
			list.addAll(myProperties.values());
			Collections.sort(list);
			return list;
		}
		return null;
	}

	// 不同的视窗不同的主页模板
	@Override
	public Object find(String resourceId, String valueId, ISubject subject,
			IServicewsContext ctx) {
		if ("contact".equals(resourceId)) {
			if (valueId.equals(root.getId())) {
				return root;
			}
			return myProperties.get(valueId);
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
	public ISoProviderHandler init(String hostId, IAclFinder finder,
			IAclSetting setting) {
		// TODO Auto-generated method stub
		root = new ContactSO();
		root.setDesc("联系人");
		root.setIcon("./startMenu/img/iconfont-dingdanxinxi.svg");
		root.setName("联系人");
		root.setId("servicews.contact");
		root.setPosition(ISurfacePosition.POSITION_TOOLBAR_WIN_CONTACT);
		root.setProvider(hostId);
		IServiceosWebsiteModule m = ServiceosWebsiteModule.get();
		try {
			byte[] b = m.context().resource("/contact.category.json");
			List<ContactSO> list = new Gson().fromJson(new String(b),
					new TypeToken<List<ContactSO>>() {
					}.getType());
			this.myProperties=new HashMap<String,ContactSO>();
			for(ContactSO so:list){
				myProperties.put(so.getId(), so);
			}
		} catch (CircuitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
