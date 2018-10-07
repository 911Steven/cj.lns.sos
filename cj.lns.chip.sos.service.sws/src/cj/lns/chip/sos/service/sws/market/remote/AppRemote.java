package cj.lns.chip.sos.service.sws.market.remote;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.BaseService;
import cj.lns.chip.sos.service.sws.market.remote.parameter.FindAppCodeParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.FindAppParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.FindAppPhyIdParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.GetMountedAppParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.GetMountedAppsParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.MountAppParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.UnmountAppParameters;
import cj.lns.chip.sos.website.AppMenu;
import cj.lns.chip.sos.website.AppPortal;
import cj.lns.chip.sos.website.AppSO;
import cj.lns.common.sos.service.model.sws.SwsApp;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.gson2.com.google.gson.reflect.TypeToken;
import cj.ultimate.util.StringUtil;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗应用市场")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/sws/market/app/", isExoteric = true)
public class AppRemote extends BaseService
		implements IRemoteService, IEntityManagerable {

	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的应用，通过应用代码和提供商", returnContentType = "text/json", returnUsage = "返回已挂载应用，404不存在")
	public RemoteResult findAppByProvider(FindAppParameters parameters)
			throws CircuitException {
		String jpql = "select a from SwsApp a where a.swsId=:swsId and a.code=:appCode and a.provider=:provider";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("appId", parameters.getAppCode());
		q.setParameter("provider", parameters.getProvider());
		try {
			Object o = q.getSingleResult();

			SwsApp sa = (SwsApp) o;
			AppSO app = new AppSO();
			app.setPlatform(sa.getPlatform());
			app.setPhyId(sa.getId());
			app.setCommand(sa.getCommand());
			app.setIcon(sa.getIcon());
			app.setId(sa.getCode());
			app.setCategory(sa.getCategory());
			app.setName(sa.getName());
			app.setTarget(sa.getTarget());
			app.setProvider(sa.getProvider());
			app.setDesc(sa.getDescription());
			app.setPosition(sa.getPosition());
			app.setHidden(sa.getHidden());
			String ajson = sa.getMenus();
			if (!StringUtil.isEmpty(ajson)) {
				List<AppMenu> menus = new Gson().fromJson(ajson,
						new TypeToken<List<AppMenu>>() {
						}.getType());
				app.getMenus().addAll(menus);
			}
			if (!StringUtil.isEmpty(sa.getPortalLeft())
					|| !StringUtil.isEmpty(sa.getPortalRight())) {
				AppPortal p = new AppPortal();
				p.setLeft(sa.getPortalLeft());
				p.setRight(sa.getPortalRight());
				app.setPortal(p);
			}
			String json = new Gson().toJson(app);
			RemoteResult result = new RemoteResult(200, "ok");
			result.content().writeBytes(json.getBytes());
			return result;
		} catch (NoResultException e) {
			return new RemoteResult(404, "应用不存在。");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的应用，通过应用标识", returnContentType = "text/json", returnUsage = "返回已挂载应用，404不存在")
	public RemoteResult findAppByPhyId(FindAppPhyIdParameters parameters)
			throws CircuitException {
		String jpql = "select a from SwsApp a where a.swsId=:swsId and a.id=:phyId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("phyId", parameters.getPhyId());
		try {
			Object o = q.getSingleResult();

			SwsApp sa = (SwsApp) o;
			AppSO app = new AppSO();
			app.setPhyId(sa.getId());
			app.setPlatform(sa.getPlatform());
			app.setCommand(sa.getCommand());
			app.setIcon(sa.getIcon());
			app.setId(sa.getCode());
			app.setCategory(sa.getCategory());
			app.setName(sa.getName());
			app.setTarget(sa.getTarget());
			app.setProvider(sa.getProvider());
			app.setDesc(sa.getDescription());
			app.setPosition(sa.getPosition());
			app.setHidden(sa.getHidden());
			String ajson = sa.getMenus();
			if (!StringUtil.isEmpty(ajson)) {
				List<AppMenu> menus = new Gson().fromJson(ajson,
						new TypeToken<List<AppMenu>>() {
						}.getType());
				app.getMenus().addAll(menus);
			}
			if (!StringUtil.isEmpty(sa.getPortalLeft())
					|| !StringUtil.isEmpty(sa.getPortalRight())) {
				AppPortal p = new AppPortal();
				p.setLeft(sa.getPortalLeft());
				p.setRight(sa.getPortalRight());
				app.setPortal(p);
			}
			String json = new Gson().toJson(app);
			RemoteResult result = new RemoteResult(200, "ok");
			result.content().writeBytes(json.getBytes());
			return result;
		} catch (NoResultException e) {
			return new RemoteResult(404, "应用不存在。");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的应用，通过应用逻辑标识", returnContentType = "text/json", returnUsage = "返回已挂载应用，404不存在")
	public RemoteResult findAppByLogicId(FindAppCodeParameters parameters)
			throws CircuitException {
		String jpql = "select a from SwsApp a where a.swsId=:swsId and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("code", parameters.getCode());
		List list = q.getResultList();
		if (list.isEmpty()) {
			return new RemoteResult(404, "应用不存在。");
		}
		Object o = list.get(0);
		SwsApp sa = (SwsApp) o;
		AppSO app = new AppSO();
		app.setPhyId(sa.getId());
		app.setPlatform(sa.getPlatform());
		app.setCommand(sa.getCommand());
		app.setIcon(sa.getIcon());
		app.setId(sa.getCode());
		app.setCategory(sa.getCategory());
		app.setName(sa.getName());
		app.setSort(sa.getSort());
		app.setTarget(sa.getTarget());
		app.setProvider(sa.getProvider());
		app.setDesc(sa.getDescription());
		app.setPosition(sa.getPosition());
		app.setHidden(sa.getHidden());
		String ajson = sa.getMenus();
		if (!StringUtil.isEmpty(ajson)) {
			List<AppMenu> menus = new Gson().fromJson(ajson,
					new TypeToken<List<AppMenu>>() {
					}.getType());
			app.getMenus().addAll(menus);
		}
		if (!StringUtil.isEmpty(sa.getPortalLeft())
				|| !StringUtil.isEmpty(sa.getPortalRight())) {
			AppPortal p = new AppPortal();
			p.setLeft(sa.getPortalLeft());
			p.setRight(sa.getPortalRight());
			app.setPortal(p);
		}
		String json = new Gson().toJson(app);
		RemoteResult result = new RemoteResult(200, "ok");
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的应用，通过应用逻辑标识", returnContentType = "text/json", returnUsage = "返回已应用物理标识，404不存在")
	public RemoteResult findPhyIdByLogicId(FindAppCodeParameters parameters)
			throws CircuitException {
		String jpql = "select a.id from SwsApp a where a.swsId=:swsId and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("code", parameters.getCode());
		List list = q.getResultList();
		if (list.isEmpty()) {
			return new RemoteResult(404, "应用不存在。");
		}
		Object o = list.get(0);
		RemoteResult result = new RemoteResult(200, "ok");
		BigInteger phyid = (BigInteger) o;
		result.head("phyid", phyid.toString());
		result.content().writeBytes(new Gson().toJson(o).getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的应用", returnContentType = "text/json", returnUsage = "返回已挂载应用列表")
	public RemoteResult getMountedApps(GetMountedAppParameters parameters)
			throws CircuitException {
		List<AppSO> list = new ArrayList<>();
		String jpql = "select a from SwsApp a where a.swsId=:swsId";
		boolean hasPos = false;
		if (!StringUtil.isEmpty(parameters.getPosition())) {
			jpql = String.format("%s and a.position=:position", jpql);
			hasPos = true;
		}
		boolean hasProvider = false;
		if (!StringUtil.isEmpty(parameters.getProvider())) {
			jpql = String.format("%s and a.provider=:provider", jpql);
			hasProvider = true;
		}
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		if (hasPos) {
			q.setParameter("position", parameters.getPosition());
		}
		if (hasProvider) {
			q.setParameter("provider", parameters.getProvider());
		}
		List<?> ret = q.getResultList();

		for (Object o : ret) {
			SwsApp sa = (SwsApp) o;
			AppSO app = new AppSO();
			app.setPhyId(sa.getId());
			app.setPlatform(sa.getPlatform());
			app.setDesc(sa.getDescription());
			app.setCommand(sa.getCommand());
			app.setIcon(sa.getIcon());
			app.setId(sa.getCode());
			app.setSort(sa.getSort());
			app.setCategory(sa.getCategory());
			app.setName(sa.getName());
			app.setTarget(sa.getTarget());
			app.setProvider(sa.getProvider());
			app.setDesc(sa.getDescription());
			app.setPosition(sa.getPosition());
			app.setHidden(sa.getHidden());
			String json = sa.getMenus();
			if (!StringUtil.isEmpty(json)) {
				List<AppMenu> menus = new Gson().fromJson(json,
						new TypeToken<List<AppMenu>>() {
						}.getType());
				app.getMenus().addAll(menus);
			}
			if (!StringUtil.isEmpty(sa.getPortalLeft())
					|| !StringUtil.isEmpty(sa.getPortalRight())) {
				AppPortal p = new AppPortal();
				p.setLeft(sa.getPortalLeft());
				p.setRight(sa.getPortalRight());
				app.setPortal(p);
			}
			list.add(app);
		}
		String json = new Gson().toJson(list);
		RemoteResult result = new RemoteResult(200, "ok");
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的应用", returnContentType = "text/json", returnUsage = "返回已挂载应用列表")
	public RemoteResult getMountedApps2(GetMountedAppsParameters parameters)
			throws CircuitException {
		List<AppSO> list = new ArrayList<>();
		String jpql = "select a from SwsApp a where a.swsId=:swsId";
		boolean hasPos = false;
		if (!StringUtil.isEmpty(parameters.getPosition())) {
			jpql = String.format("%s and a.position=:position", jpql);
			hasPos = true;
		}
		boolean hasPlatform = false;
		if (!StringUtil.isEmpty(parameters.getPlatform())) {
			jpql = String.format("%s and a.platform=:platform", jpql);
			hasPlatform = true;
		}
		if (!StringUtil.isEmpty(parameters.getProviders())) {
			String provider = String.format("('%s')",
					parameters.getProviders().replace(",", "','"));
			jpql = String.format("%s and a.provider in %s", jpql,provider);
		}
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		if (hasPos) {
			q.setParameter("position", parameters.getPosition());
		}
		if (hasPlatform) {
			q.setParameter("platform", parameters.getPlatform());
		}
		List<?> ret = q.getResultList();

		for (Object o : ret) {
			SwsApp sa = (SwsApp) o;
			AppSO app = new AppSO();
			app.setPhyId(sa.getId());
			app.setPlatform(sa.getPlatform());
			app.setDesc(sa.getDescription());
			app.setCommand(sa.getCommand());
			app.setIcon(sa.getIcon());
			app.setId(sa.getCode());
			app.setSort(sa.getSort());
			app.setCategory(sa.getCategory());
			app.setName(sa.getName());
			app.setTarget(sa.getTarget());
			app.setProvider(sa.getProvider());
			app.setDesc(sa.getDescription());
			app.setPosition(sa.getPosition());
			app.setHidden(sa.getHidden());
			String json = sa.getMenus();
			if (!StringUtil.isEmpty(json)) {
				List<AppMenu> menus = new Gson().fromJson(json,
						new TypeToken<List<AppMenu>>() {
						}.getType());
				app.getMenus().addAll(menus);
			}
			if (!StringUtil.isEmpty(sa.getPortalLeft())
					|| !StringUtil.isEmpty(sa.getPortalRight())) {
				AppPortal p = new AppPortal();
				p.setLeft(sa.getPortalLeft());
				p.setRight(sa.getPortalRight());
				app.setPortal(p);
			}
			list.add(app);
		}
		String json = new Gson().toJson(list);
		RemoteResult result = new RemoteResult(200, "ok");
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "挂载指定的应用", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult mountApp(MountAppParameters parameters)
			throws CircuitException {
		String json = new String(parameters.getContent().readFully());
		AppSO app = new Gson().fromJson(json, AppSO.class);
		String jpql = "select a from SwsApp a where a.swsId=:swsId and a.provider=:provider and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("provider", app.getProvider());
		q.setParameter("code", app.getId());
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsApp sa = new SwsApp();
			sa.setCode(app.getId());
			sa.setPlatform(app.getPlatform());
			sa.setDescription(app.getDesc());
			sa.setCategory(app.getCategory());
			sa.setCommand(app.getCommand());
			sa.setIcon(app.getIcon());
			sa.setSort(app.getSort());
			sa.setName(app.getName());
			sa.setProvider(app.getProvider());
			sa.setSwsId(parameters.getSwsId());
			sa.setTarget(app.getTarget());
			sa.setPosition(app.getPosition());
			sa.setHidden(app.getHidden());
			sa.setMenus(new Gson().toJson(app.getMenus()));
			if (app.getPortal() != null) {
				sa.setPortalRight(app.getPortal().getRight());
				sa.setPortalLeft(app.getPortal().getLeft());
			}
			em.persist(sa);
		}
		return new RemoteResult(200, "ok");
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "卸载指定的应用", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult unmountApp(UnmountAppParameters parameters)
			throws CircuitException {
		String jpql = "delete from SwsApp a where a.swsId=:swsId and a.provider=:provider and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("provider", parameters.getProvider());
		q.setParameter("code", parameters.getAppCode());
		q.executeUpdate();
		return new RemoteResult(200, "ok");
	}
}
