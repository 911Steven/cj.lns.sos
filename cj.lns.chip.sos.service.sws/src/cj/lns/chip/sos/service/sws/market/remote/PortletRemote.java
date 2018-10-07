package cj.lns.chip.sos.service.sws.market.remote;

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
import cj.lns.chip.sos.service.sws.market.remote.parameter.FindPortletParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.GetMountedPortletParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.MountPortletParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.UnmountPortletParameters;
import cj.lns.chip.sos.website.PortletSO;
import cj.lns.common.sos.service.model.sws.SwsPortlet;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗栏目市场")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/sws/market/portlet/", isExoteric = true)
public class PortletRemote extends BaseService
		implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的指定物理标识的栏目", returnContentType = "text/json", returnUsage = "返回已挂载栏目")
	public RemoteResult findPortlet(FindPortletParameters parameters)
			throws CircuitException {
		String jpql = "select a from SwsPortlet a where a.swsId=:swsId and a.id=:phyId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("phyId", parameters.getPhyId());
		try {
			Object o = q.getSingleResult();
			SwsPortlet sa = (SwsPortlet) o;
			PortletSO let = new PortletSO();
			let.setCategory(sa.getCategory());
			let.setUseTemplate(sa.getUseTemplate());
			let.setDescription(sa.getDescription());
			let.setProvider(sa.getProvider());
			let.setContentUrl(sa.getContentUrl());
			let.setPhyId(sa.getId());
			let.setName(sa.getName());
			let.setPosition(sa.getPosition());
			let.setId(sa.getCode());
			let.setSort(sa.getSort());
			let.setIcon(sa.getIcon());
			String json = new Gson().toJson(let);
			RemoteResult result = new RemoteResult(200, "ok");
			result.content().writeBytes(json.getBytes());
			return result;
		} catch (NoResultException e) {
			return new RemoteResult(404, "栏目不存在");
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的栏目", returnContentType = "text/json", returnUsage = "返回已挂载栏目列表")
	public RemoteResult getMountedPortlets(
			GetMountedPortletParameters parameters) throws CircuitException {
		List<PortletSO> list = new ArrayList<>();
		String jpql = "select a from SwsPortlet a where a.swsId=:swsId";
		if (!StringUtil.isEmpty(parameters.getPosition())) {
			jpql = String.format("%s and a.position='%s'",
					jpql,parameters.getPosition());
		}
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());

		List<?> ret = q.getResultList();

		for (Object o : ret) {
			SwsPortlet sa = (SwsPortlet) o;
			PortletSO let = new PortletSO();
			let.setCategory(sa.getCategory());
			let.setUseTemplate(sa.getUseTemplate());
			let.setDescription(sa.getDescription());
			let.setProvider(sa.getProvider());
			let.setPhyId(sa.getId());
			let.setName(sa.getName());
			let.setContentUrl(sa.getContentUrl());
			let.setPosition(sa.getPosition());
			let.setId(sa.getCode());
			let.setSort(sa.getSort());
			let.setIcon(sa.getIcon());
			list.add(let);
		}
		String json = new Gson().toJson(list);
		RemoteResult result = new RemoteResult(200, "ok");
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "挂载指定的栏目", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult mountPortlet(MountPortletParameters parameters)
			throws CircuitException {
		String json = new String(parameters.getContent().readFully());
		PortletSO let = new Gson().fromJson(json, PortletSO.class);
		String jpql = "select a from SwsPortlet a where a.swsId=:swsId and a.provider=:provider and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("provider", let.getProvider());
		q.setParameter("code", let.getId());
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsPortlet sa = new SwsPortlet();
			sa.setCategory(let.getCategory());
			sa.setUseTemplate(let.getUseTemplate());
			sa.setDescription(let.getDescription());
			sa.setProvider(let.getProvider());
			sa.setId(let.getPhyId());
			sa.setName(let.getName());
			sa.setContentUrl(let.getContentUrl());
			sa.setPosition(let.getPosition());
			sa.setCode(let.getId());
			sa.setSort(let.getSort());
			sa.setIcon(let.getIcon());
			sa.setSwsId(parameters.getSwsId());
			em.persist(sa);
		}
		return new RemoteResult(200, "ok");
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "卸载指定的栏目", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult unmountPortlet(UnmountPortletParameters parameters)
			throws CircuitException {
		String jpql = "delete from SwsPortlet a where a.swsId=:swsId and a.provider=:provider and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("provider", parameters.getProvider());
		q.setParameter("code", parameters.getCode());
		q.executeUpdate();
		return new RemoteResult(200, "ok");
	}
}
