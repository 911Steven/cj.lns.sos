package cj.lns.chip.sos.service.framework.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.FrameworkInfo;
import cj.lns.chip.sos.service.PluginInfo;
import cj.lns.chip.sos.service.framework.remote.parameter.CreateServicewsParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.ExistsSwsTemplateInPortalParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.FindPortalInfoParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.FrameworkReporterParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.SetUserRegisterDefaultTemplateParameters;
import cj.lns.chip.sos.service.portal.PortalInfo;
import cj.lns.chip.sos.service.portal.SceneInfo;
import cj.lns.common.sos.service.model.SosAppHost;
import cj.lns.common.sos.service.model.SosMenuHost;
import cj.lns.common.sos.service.model.SosPlugin;
import cj.lns.common.sos.service.model.SosPortal;
import cj.lns.common.sos.service.model.SosPortletHost;
import cj.lns.common.sos.service.model.SosProperty;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.model.sws.SwsOwnerProfile;
import cj.lns.common.sos.service.model.sws.SwsPortalConf;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "frameworkService")
public class FrameworkService implements IEntityManagerable, IFrameworkService {
	@CjServiceRef(refByName = "idGenerator")
	IIDGenerator id;
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}
	
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#createServicews(cj.lns.chip.sos.service.framework.remote.parameter.CreateServicewsParameters)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public void createServicews(CreateServicewsParameters parameters)
			throws CircuitException {
		String jpql = "select si from SwsInfo si where si.owner=:userCode and si.usePortal=:portalId";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getCreator());
		q.setParameter("portalId", parameters.getPortalId());
		try {
			SwsInfo si = (SwsInfo) q.getSingleResult();
			throw new CircuitException("500",
					String.format("用户%s在框架%s下已存在视窗%s", parameters.getCreator(),
							parameters.getPortalId(), si.getId()));
		} catch (NoResultException e) {

		}
		jpql = "select u from SosUser u where u.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getCreator());
		SosUser user = null;
		try {
			user = (SosUser) q.getSingleResult();
		} catch (NoResultException e) {
			new CircuitException("500",
					String.format("用户%s不存在", parameters.getCreator()));
		}

		PortalInfo pi = findPortalBaseInfo(parameters.getPortalId());
		SwsInfo si = new SwsInfo();

		BigInteger bi = this.id.genSwsId(parameters.getKindCode(),
				parameters.getUserSetCode());
		si.setId(bi);
		si.setInheritId(BigInteger.valueOf(-1));
		si.setName(pi.getInfo().getAssemblyTitle());
		si.setOwner(parameters.getCreator());
		si.setUsePortal(pi.getInfo().getAssemblyGuid());
		si.setDescription(pi.getInfo().getAssemblyDescription());
		em.persist(si);
		em.flush();
		BigInteger swsId = si.getId();

		SwsPortalConf spc = new SwsPortalConf();
		spc.setPortalId(si.getUsePortal());
		spc.setSwsId(swsId);
		if (pi.getScenes().isEmpty()) {
			throw new CircuitException("501",
					String.format("框架%s下缺少场景定义", parameters.getPortalId()));
		}
		if (pi.getThemes().isEmpty()) {
			throw new CircuitException("501",
					String.format("框架%s下缺少主题定义", parameters.getPortalId()));
		}
		// 先分给默认的场景和主题，待用户进入后修改
		SceneInfo scene = null;
		String defaultScene = String.format("%s.scene",
				parameters.getSceneName());
		for (SceneInfo o : pi.getScenes()) {
			String sceneId = String.format("%s.%s.scene",
					parameters.getSceneName(), parameters.getTerminus());
			if (o.getSceneName().equals(sceneId)
					|| o.getSceneName().equals(defaultScene)) {
				scene = o;
				break;
			}
		}
		if (scene == null) {
			throw new CircuitException("404", String.format("要求的终端%s场景%s不存在",
					parameters.getTerminus(), parameters.getSceneName()));
		}
		spc.setUseCanvas(parameters.getSceneName());
		spc.setUseSceneId(parameters.getSceneName());
		List<String> themes = pi.getThemes().get(parameters.getTerminus());
		if (themes == null || themes.isEmpty()
				|| !themes.contains(parameters.getThemeName())) {
			throw new CircuitException("404", String.format("要求的终端%s主题%s不存在",
					parameters.getTerminus(), parameters.getThemeName()));
		}
		String themeName = parameters.getThemeName();
		spc.setUseTheme(themeName);
		em.persist(spc);
		// 场景主题结束
		SwsOwnerProfile profile = new SwsOwnerProfile();
		profile.setSwsId(swsId);
		em.persist(profile);

		if (parameters.isAsTemplate()) {
			jpql = "update from SosPortal p set p.swsTemplateId=:templateId where p.pluginGuid=:portalId";
			q = em.createQuery(jpql);
			q.setParameter("templateId", swsId);
			q.setParameter("portalId", pi.getInfo().getAssemblyGuid());
			q.executeUpdate();
		}
	}

	private PortalInfo findPortalBaseInfo(String portalId)
			throws CircuitException {
		String jpql = "select plug from SosPlugin plug where plug.assemblyGuid=:portalId";
		Query q = em.createQuery(jpql);
		q.setParameter("portalId", portalId);
		SosPlugin plugin = null;
		try {
			plugin = (SosPlugin) q.getSingleResult();
		} catch (NoResultException e) {
			throw new CircuitException("404",
					String.format("框架%s 在插件表中不存在", portalId));
		}

		jpql = "select p from SosPortal p where p.pluginGuid=:portalId";
		q = em.createQuery(jpql);
		q.setParameter("portalId", portalId);
		SosPortal portal = null;
		try {
			portal = (SosPortal) q.getSingleResult();
		} catch (NoResultException e) {
			throw new CircuitException("404",
					String.format("框架%s 在框架信息表中不存在", portalId));
		}

		PortalInfo pi = new PortalInfo();
		PluginInfo pl = new PluginInfo();
		pl.setAssemblyCompany(plugin.getAssemblyCompany());
		pl.setAssemblyCopyright(plugin.getAssemblyCopyright());
		pl.setAssemblyDescription(plugin.getAssemblyDescription());
		pl.setAssemblyDeveloperHome(plugin.getAssemblyDeveloperHome());
		pl.setAssemblyGuid(plugin.getAssemblyGuid());
		pl.setAssemblyIcon(plugin.getAssemblyIcon());
		pl.setAssemblyIconBytes(plugin.getAssemblyIconBytes());
		pl.setAssemblyProduct(plugin.getAssemblyProduct());
		pl.setAssemblyTitle(plugin.getAssemblyTitle());
		pl.setAssemblyVersion(plugin.getAssemblyVersion());
		pl.setDependencyPlugin(plugin.getDependencyPlugin());
		pi.setInfo(pl);
		pi.setSwsTemplateId(portal.getSwsTemplateId());
		List<SceneInfo> scenes = new Gson().fromJson(portal.getScenes(),
				new TypeToken<List<SceneInfo>>() {
				}.getType());
		pi.getScenes().addAll(scenes);
		Map<String, List<String>> themes = new Gson().fromJson(
				portal.getThemes(), new TypeToken<Map<String, List<String>>>() {
				}.getType());
		pi.getThemes().putAll(themes);

		return pi;
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#findPortalInfo(cj.lns.chip.sos.service.framework.remote.parameter.FindPortalInfoParameters)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public PortalInfo findPortalInfo(FindPortalInfoParameters parameters)
			throws CircuitException {

		PortalInfo pi = findPortalBaseInfo(parameters.getPortalId());

		String hql = "select a from SosAppHost a where a.portalId=:portalId";
		Query q = em.createQuery(hql);
		q.setParameter("portalId", parameters.getPortalId());
		List<?> maket = q.getResultList();
		for (Object o : maket) {
			SosAppHost a = (SosAppHost) o;
			pi.getApps().add(a.getPluginGuid());
		}
		hql = "select a from SosPortletHost a where a.portalId=:portalId";
		q = em.createQuery(hql);
		q.setParameter("portalId", parameters.getPortalId());
		maket = q.getResultList();
		for (Object o : maket) {
			SosPortletHost a = (SosPortletHost) o;
			pi.getLets().add(a.getPluginGuid());
		}
		hql = "select a from SosMenuHost a where a.portalId=:portalId";
		q = em.createQuery(hql);
		q.setParameter("portalId", parameters.getPortalId());
		maket = q.getResultList();
		for (Object o : maket) {
			SosMenuHost a = (SosMenuHost) o;
			pi.getMenus().add(a.getPluginGuid());
		}
		return pi;
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#existsSwsTemplateInPortal(cj.lns.chip.sos.service.framework.remote.parameter.ExistsSwsTemplateInPortalParameters)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public boolean existsSwsTemplateInPortal(
			ExistsSwsTemplateInPortalParameters parameters)
					throws CircuitException {
		String jpql = "select count(p) from SosPortal p where p.pluginGuid=:portalId and p.swsTemplateId IS not null";
		Query q = em.createQuery(jpql);
		q.setParameter("portalId", parameters.getPortalId());
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#getSosVersion()
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public List<?> getSosVersion() {
		String jpql = "select p from SosProperty p";
		Query qsosp = em.createQuery(jpql);
		List<?> qsosList = qsosp.getResultList();
		return qsosList;
	}
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#getPortals()
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public List<PortalInfo> getPortals() throws CircuitException {
		List<PortalInfo> all = new ArrayList<PortalInfo>();
		String jpql = "select p from SosPortal p";
		Query q = em.createQuery(jpql);
		List<?> list = q.getResultList();
		for (Object o : list) {
			SosPortal p = (SosPortal) o;
			FindPortalInfoParameters a = new FindPortalInfoParameters();
			a.setPortalId(p.getPluginGuid());
			PortalInfo pi = this.findPortalInfo(a);
			all.add(pi);
		}
		return all;
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#setUserRegisterDefaultTemplate(cj.lns.chip.sos.service.framework.remote.parameter.SetUserRegisterDefaultTemplateParameters)
	 */
	@Override
	// sos_porperties user.register.sws.template
	@CjTransaction(unitName = "sosdb")
	public void setUserRegisterDefaultTemplate(
			SetUserRegisterDefaultTemplateParameters parameters)
					throws CircuitException {
		String jpql = "delete from SosProperty p where p.propName='user.register.sws.template'";
		Query q = em.createQuery(jpql);
		q.executeUpdate();
		SosProperty p = new SosProperty();
		p.setPropName("user.register.sws.template");
		p.setPropValue(String.valueOf(parameters.getSwsid()));
		p.setDescription("为用户注册时分配初始视窗");
		em.persist(p);
	}

	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IFrameworkService#report(cj.lns.chip.sos.service.framework.remote.parameter.FrameworkReporterParameters)
	 */
	@Override
	@CjTransaction(unitName = "sosdb")
	public void report(FrameworkReporterParameters parameters)
			throws CircuitException {
		Gson g = new Gson();
		byte[] b = parameters.getContent().readFully();
		String json = new String(b);
		FrameworkInfo fi = g.fromJson(json, FrameworkInfo.class);
		persistenceFramework(parameters, fi);
	}

	private void persistenceFramework(FrameworkReporterParameters parameters,
			FrameworkInfo fi) throws CircuitException {
		List<PluginInfo> plugins = fi.getPlugins();
		for (PluginInfo pi : plugins) {
			updatePlugin(pi, em);
			if (pi.getAssemblyGuid().equals(fi.getWebsiteId())) {
				updateWebsite(pi, fi, em);
			}
		}
		Map<String, PortalInfo> portals = fi.getPortals();
		Set<Entry<String, PortalInfo>> set = portals.entrySet();
		for (Entry<String, PortalInfo> en : set) {
			updatePortal(parameters, en.getKey(), en.getValue(), fi, em);
		}
	}

	private void updateWebsite(PluginInfo pi, FrameworkInfo fi,
			EntityManager em) {
		String hql = "select p from SosProperty p where p.propName='sos.website.id'";
		Query q = em.createQuery(hql);
		List<?> ret = q.getResultList();
		if (ret.isEmpty()) {
			SosProperty sp = new SosProperty();
			sp.setPropName("sos.website.id");
			sp.setPropValue(fi.getWebsiteId());
			sp.setDescription("serviceos website 的插件标识");
			em.persist(sp);
		} else {
			SosProperty sp = (SosProperty) ret.get(0);
			em.refresh(sp);
			sp.setPropValue(fi.getWebsiteId());
			sp.setDescription("serviceos website 的插件标识");
			em.merge(sp);
		}
	}

	private void updatePlugin(PluginInfo pi, EntityManager em) {
		String hql = "select p from SosPlugin p where p.assemblyGuid =:assemblyGuid";
		Query q = em.createQuery(hql);
		q.setParameter("assemblyGuid", pi.getAssemblyGuid());
		List<?> ret = q.getResultList();
		if (ret.isEmpty()) {
			SosPlugin p = new SosPlugin();
			fillPlugin(pi, p);
			em.persist(p);
		} else {
			SosPlugin p = (SosPlugin) ret.get(0);
			fillPlugin(pi, p);
			em.merge(p);
		}
	}

	private void fillPlugin(PluginInfo pi, SosPlugin p) {
		p.setAssemblyCompany(pi.getAssemblyCompany());
		p.setAssemblyCopyright(pi.getAssemblyCopyright());
		p.setAssemblyDescription(pi.getAssemblyDescription());
		p.setAssemblyDeveloperHome(pi.getAssemblyDeveloperHome());
		p.setAssemblyGuid(pi.getAssemblyGuid());
		p.setAssemblyIcon(pi.getAssemblyIcon());
		// AssemblyIconBytes b=new AssemblyIconBytes();
		// b.setB(pi.getAssemblyIconBytes());
		p.setAssemblyIconBytes(pi.getAssemblyIconBytes());
		p.setAssemblyProduct(pi.getAssemblyProduct());
		p.setAssemblyTitle(pi.getAssemblyTitle());
		p.setAssemblyVersion(pi.getAssemblyVersion());
		p.setDependencyPlugin(pi.getDependencyPlugin());
	}

	private void updatePortal(FrameworkReporterParameters parameters,
			String portalid, PortalInfo pi, FrameworkInfo fi,
			EntityManager em) {
		String hql = "select p from SosPortal p where p.pluginGuid=:id";
		Query q = em.createQuery(hql);
		q.setParameter("id", portalid);
		List<?> ret = q.getResultList();
		if (ret.isEmpty()) {
			SosPortal p = new SosPortal();
			fillPortalSceneAndThemes(pi, p, portalid);
			em.persist(p);
		} else {
			SosPortal p = (SosPortal) ret.get(0);
			fillPortalSceneAndThemes(pi, p, portalid);
			em.merge(p);
		}
		List<String> apps = pi.getApps();
		for (String id : apps) {
			hql = "select a from SosAppHost a where a.pluginGuid=:id";
			q = em.createQuery(hql);
			q.setParameter("id", id);
			ret = q.getResultList();
			if (ret.isEmpty()) {
				SosAppHost e = new SosAppHost();
				e.setPluginGuid(id);
				e.setCreateTime(new Date());
				e.setOwner(parameters.getUserCode());
				e.setPortalId(portalid);
				em.persist(e);
			} else {
				SosAppHost e = (SosAppHost) ret.get(0);
				e.setPluginGuid(id);
				e.setCreateTime(new Date());
				e.setOwner(parameters.getUserCode());
				e.setPortalId(portalid);
				em.merge(e);
			}
		}
		List<String> lets = pi.getLets();
		for (String id : lets) {
			hql = "select a from SosPortletHost a where a.pluginGuid=:id";
			q = em.createQuery(hql);
			q.setParameter("id", id);
			ret = q.getResultList();
			if (ret.isEmpty()) {
				SosPortletHost e = new SosPortletHost();
				e.setPluginGuid(id);
				e.setCreateTime(new Date());
				e.setOwner(parameters.getUserCode());
				e.setPortalId(portalid);
				em.persist(e);
			} else {
				SosPortletHost e = (SosPortletHost) ret.get(0);
				e.setPluginGuid(id);
				e.setCreateTime(new Date());
				e.setOwner(parameters.getUserCode());
				e.setPortalId(portalid);
				em.merge(e);
			}
		}
		List<String> menus = pi.getMenus();
		for (String id : menus) {
			hql = "select a from SosMenuHost a where a.pluginGuid=:id";
			q = em.createQuery(hql);
			q.setParameter("id", id);
			ret = q.getResultList();
			if (ret.isEmpty()) {
				SosMenuHost e = new SosMenuHost();
				e.setPluginGuid(id);
				e.setCreateTime(new Date());
				e.setOwner(parameters.getUserCode());
				e.setPortalId(portalid);
				em.persist(e);
			} else {
				SosMenuHost e = (SosMenuHost) ret.get(0);
				e.setPluginGuid(id);
				e.setCreateTime(new Date());
				e.setOwner(parameters.getUserCode());
				e.setPortalId(portalid);
				em.merge(e);
			}
		}
	}

	private void fillPortalSceneAndThemes(PortalInfo pi, SosPortal p,
			String portalid) {
		p.setPluginGuid(portalid);
		p.setCreateTime(new Date());
		String scenes = new Gson().toJson(pi.getScenes());
		p.setScenes(scenes);
		String themes = new Gson().toJson(pi.getThemes());
		p.setThemes(themes);

	}

	
	
}
