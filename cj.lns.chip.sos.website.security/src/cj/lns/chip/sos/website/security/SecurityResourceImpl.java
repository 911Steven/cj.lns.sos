package cj.lns.chip.sos.website.security;

import java.util.ArrayList;
import java.util.List;

import cj.lns.chip.sos.sws.security.IAclFinder;
import cj.lns.chip.sos.sws.security.IAclSetting;
import cj.lns.chip.sos.sws.security.ISecurityObject;
import cj.lns.chip.sos.sws.security.ISecurityObjectProvider;
import cj.lns.chip.sos.website.framework.IServicewsContext;
import cj.lns.chip.sos.website.framework.ISubject;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.logging.ILogging;

public class SecurityResourceImpl implements ISecurityResourceImpl {

	private String hostId;
	ISecurityObjectProvider provider;
	private ISecurityObject root;// 代表当前资源的定义
	private IAclFinder finder;
	private IAclSetting setting;
	ILogging log;

	public SecurityResourceImpl(String hostId, ISecurityObject root,
			ISecurityObjectProvider p, IAclFinder finder, IAclSetting setting) {
		this.hostId = hostId;
		this.provider = p;
		this.root = root;
		this.finder = finder;
		this.setting = setting;
		log = CJSystem.current().environment().logging();
	}
	@Override
	public Class<?> soProviderClass() {
		return provider.getClass();
	}
	public IAclFinder finder() {
		return this.finder;
	}

	@Override
	public IAclSetting setting() {
		return this.setting;
	}

	public String getHostId() {
		return hostId;
	}

	@Override
	public ISecurityObject root() {
		return root;
	}

	@Override
	public ISecurityObject find(String resourceId, String valueId,
			ISubject subject, IServicewsContext ctx) {
		try {
			Object o = provider.find(resourceId, valueId, subject, ctx);
			if (o == null)
				return null;

			return ISecurityObject.securityObject(o);
		} catch (Exception e) {
			log.error(getClass(), e);
			throw new EcmException(e);
		}
	}

	@Override
	public List<ISecurityObject> childs(String sobjectId, String instId,
			ISubject subject, IServicewsContext ctx) {
		try {
			List<?> inner = provider.getChilds(sobjectId, instId, subject, ctx);
			List<ISecurityObject> list = new ArrayList<>();
			if (inner == null) {
				return list;
			}
			for (Object o : inner) {
				ISecurityObject so = ISecurityObject.securityObject(o);
				list.add(so);
			}
			return list;
		} catch (Exception e) {
			log.error(getClass(), e);
			throw new EcmException(e);
		}
	}

	@Override
	public boolean isRootVisible(ISecurityObject root, ISubject subject,
			IServicewsContext ctx) {
		if (root == null)
			return false;
		try {
			return provider.isRootVisible(root, subject, ctx);
		} catch (Exception e) {
			log.error(getClass(), e);
			throw new EcmException(e);
		}
	}

	@Override
	public String resourceId() {
		// TODO Auto-generated method stub
		return this.root.resourceId();
	}

	@Override
	public String resourceName() {
		// TODO Auto-generated method stub
		return root.resourceName();
	}

	@Override
	public String valueId() {
		// TODO Auto-generated method stub
		return root.valueId();
	}

	@Override
	public String valueName() {
		// TODO Auto-generated method stub
		return root.valueName();
	}
}
