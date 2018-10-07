package cj.lns.common.sos.website.moduleable;

import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.disk.NetDisk;
import cj.lns.chip.sos.website.framework.IDatabaseCloud;
import cj.lns.chip.sos.website.framework.IServiceosModuleServiceProvider;
import cj.studio.ecm.IChip;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceSite;
import cj.studio.ecm.graph.IPin;
import cj.studio.ecm.plugins.moduleable.IModuleServiceSite;

@CjService(name = "moduleServiceSite")
public class ModuleServiceSite
		implements IModuleServiceSite, IServiceosModuleServiceProvider {
	@CjServiceSite
	private IServiceSite site;
	private IServiceProvider sosprovider;
	IPin out;
	IPin in;

	@Override
	public IPin out() {
		return out;
	}

	@Override
	public IPin in() {
		return in;
	}

	@Override
	public String contextPath() {
		IChip chip = (IChip) getService(IChip.class.getName());
		return String.format("/%s/", chip.info().getId());
	}

	@Override
	public INetDisk diskOwner(String owner) {
		IDatabaseCloud cs=(IDatabaseCloud)sosprovider.getService("databaseCloud");
		INetDisk disk=NetDisk.trustOpen(cs.userClient(), owner);//一个用户一个网盘，用户的一个视窗一个存储空间
		return disk;
	}
	@Override
	public INetDisk diskLnsSystem() {
		IDatabaseCloud cs=(IDatabaseCloud)sosprovider.getService("databaseCloud");
		INetDisk disk=cs.getLnsSysDisk();
		return disk;
	}
	@Override
	public INetDisk diskLnsData() {
		IDatabaseCloud cs=(IDatabaseCloud)sosprovider.getService("databaseCloud");
		INetDisk disk=cs.getLnsDataDisk();
		return disk;
	}
	@Override
	public <T> ServiceCollection<T> getServices(Class<T> serviceClazz) {
		ServiceCollection<T> col = site.getServices(serviceClazz);
		ServiceCollection<T> col2 = sosprovider.getServices(serviceClazz);
		return (ServiceCollection<T>) col.combine(col2);
	}

	@Override
	public Object getService(String serviceId) {
		Object service = site.getService(serviceId);
		if (service == null) {
			service = sosprovider.getService(serviceId);
		}
		return service;
	}

	@Override
	public void setFrameworkServiceProvider(IServiceProvider provider) {
		this.sosprovider = provider;
	}

}
