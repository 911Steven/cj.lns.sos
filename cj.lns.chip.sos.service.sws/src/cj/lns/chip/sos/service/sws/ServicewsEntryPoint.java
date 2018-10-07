package cj.lns.chip.sos.service.sws;

import java.util.HashMap;
import java.util.Map;

import cj.lns.chip.sos.sws.security.ISecuritySubject;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IEntryPointActivator;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.context.IElement;

public class ServicewsEntryPoint implements IEntryPointActivator {

	@Override
	public void activate(IServiceSite site,IElement args) {
		ServiceCollection<ISecuritySubject> subjects = site
				.getServices(ISecuritySubject.class);
		Map<String, ISecuritySubject> map = new HashMap<String, ISecuritySubject>();
		for (ISecuritySubject sub : subjects) {
			if (map.containsKey(sub.subjectName())) {
				throw new EcmException(String.format("有重复定义的安全主体：%s",
						sub.subjectName()));
			}
			map.put(sub.subjectName(), sub);
		}
		site.addService("subjects", map);
	}

	@Override
	public void inactivate(IServiceSite site) {
		// TODO Auto-generated method stub

	}

}
