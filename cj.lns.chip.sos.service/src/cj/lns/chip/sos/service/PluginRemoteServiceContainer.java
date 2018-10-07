package cj.lns.chip.sos.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import cj.lns.chip.sos.service.db.IDatabase;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.IRemoteServiceContainer;
import cj.lns.chip.sos.service.framework.RemoteServiceFramework;
import cj.studio.ecm.CJSystem;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.IAssembly;
import cj.studio.ecm.IAssemblyDependency;
import cj.studio.ecm.IServiceAfter;
import cj.studio.ecm.IServiceProvider;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.ServiceCollection;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.logging.ILogging;
import cj.studio.ecm.plugins.IAssemblyPlugins;
import cj.studio.ecm.plugins.IPluginDependencyEvent;
import cj.studio.ecm.plugins.moduleable.IModule;
import cj.studio.ecm.plugins.moduleable.ModuleContainer;

@CjService(name = "pluginRemoteServiceContainer")
public class PluginRemoteServiceContainer extends ModuleContainer
		implements IPluginDependencyEvent ,IServiceAfter{
	ILogging log;
	@CjServiceRef(refByName = "remoteServiceFramework")
	RemoteServiceFramework framework;
	@CjServiceRef
	SendServiceCenter sendServiceCenter;
	MyServiceProvider provider;
	IDatabaseCloud db;
	public PluginRemoteServiceContainer() {
		log = CJSystem.current().environment().logging();
		provider=new MyServiceProvider();
	}
	@Override
	public void onAfter(IServiceSite site) {
		db = (IDatabaseCloud) site
				.getService("databaseCloud");
		String homeDir = site.getProperty("home.dir");
		initDatabaseCloud(db,site,homeDir);
	}
	private void initDatabaseCloud(IDatabaseCloud db, IServiceSite site, String homeDir) {
		String fileName = String.format("%s%sconfig%sdatabase-cloud.properties",
				homeDir, File.separator, File.separator);
		File f = new File(fileName);
		if (!f.exists()) {
			throw new EcmException(
					String.format("lns云数据库未配置，应在应用启动目录下建立文件:%s", f));
		}
		String dimsConfigFile=String.format("%s%sconfig%ssystem-dims.bson", homeDir, File.separator, File.separator);
		if (!new File(dimsConfigFile).exists()) {
			throw new EcmException(
					String.format("lns云数据库未发现维度文件，请检查维度文件是否存在:%s", f));
		}
		Properties props = new Properties();
		
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			props.load(in);
		} catch (IOException e) {
			throw new EcmException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		props.setProperty("dimsConfigFile", dimsConfigFile);
		db.init(props);
	}
	
	@Override
	protected IServiceProvider getFrameworkServiceProvider() {
		return provider;
	}

	@Override
	protected void onContainerReady(IAssemblyPlugins root, IServiceSite site) {
		super.onContainerReady(root, site);
		framework.load(site);
		IAssembly the = root.find("4093DC91-BB62-4D26-B405-5464F2A4D042");
		if (the == null) {
			throw new EcmException("平台未有database插件");
		}
		ServiceCollection<IDatabase> col2 = the.workbin().part(IDatabase.class);
		if (col2.isEmpty()) {
			throw new EcmException("db插件必须定义名为datasourceService的外部服务");
		}
		provider.parent = col2.get(0);

		String ids[] = super.enumModuleName();
		for (String id : ids) {
			IRemoteServiceContainer container = framework.container();
			IModule m = super.module(id);
			m.site().out().plugLast("sendServiceCenter", sendServiceCenter);
			m.chip().site().addService("databaseCloud", db);
			ServiceCollection<IRemoteService> remotes = m.site()
					.getServices(IRemoteService.class);
			try {
				container.append(remotes);
			} catch (Exception e) {
				log.error(getClass(), e);
			}
		}
	}

	@Override
	protected void onModuleReady(IAssembly the, IModule m,
			Map<String, IModule> modules, IAssembly parent,
			IAssemblyDependency root) {
		super.onModuleReady(the, m, modules, parent, root);

	}
	private class MyServiceProvider implements IServiceProvider{
		public IDatabase parent;

		@Override
		public Object getService(String serviceId) {
			if(parent==null)return null;
			return parent.getService(serviceId);
		}
		
		@Override
		public <T> ServiceCollection<T> getServices(Class<T> serviceClazz) {
			if(parent==null)return new ServiceCollection<>();
			return parent.getServices(serviceClazz);
		}
	}
}
