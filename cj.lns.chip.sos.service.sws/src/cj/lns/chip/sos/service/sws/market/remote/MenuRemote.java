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
import cj.lns.chip.sos.service.sws.market.remote.parameter.GetMountedMenuParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.MountMenuParameters;
import cj.lns.chip.sos.service.sws.market.remote.parameter.UnmountMenuParameters;
import cj.lns.chip.sos.website.MenuSO;
import cj.lns.common.sos.service.model.sws.SwsMenu;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗菜单市场")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/sws/market/menu/", isExoteric = true)
public class MenuRemote extends BaseService
		implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取视窗已挂载的菜单", returnContentType = "text/json", returnUsage = "返回已挂载菜单列表")
	public RemoteResult getMountedMenus(
			GetMountedMenuParameters parameters) throws CircuitException {
		List<MenuSO> list = new ArrayList<>();
		String jpql = "select a from SwsMenu a where a.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		List<?> ret = q.getResultList();

		for (Object o : ret) {
			SwsMenu sa = (SwsMenu) o;
			MenuSO menu = new MenuSO();
			menu.setPhyId(sa.getId());
			menu.setDesc(sa.getDescription());
			menu.setCommand(sa.getCommand());
			menu.setIcon(sa.getIcon());
			menu.setId(sa.getCode());
			menu.setCategory(sa.getCategory());
			menu.setName(sa.getName());
			menu.setTarget(sa.getTarget());
			menu.setProvider(sa.getProvider());
			menu.setPosition(sa.getPosition());
			list.add(menu);
		}
		String json = new Gson().toJson(list);
		RemoteResult result = new RemoteResult(200, "ok");
		result.content().writeBytes(json.getBytes());
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "挂载指定的菜单", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult mountMenu(MountMenuParameters parameters)
			throws CircuitException {
		String json = new String(parameters.getContent().readFully());
		MenuSO menu = new Gson().fromJson(json, MenuSO.class);
		String jpql = "select a from SwsMenu a where a.swsId=:swsId and a.provider=:provider and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("provider", menu.getProvider());
		q.setParameter("code", menu.getId());
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsMenu sa = new SwsMenu();
			sa.setId(menu.getPhyId());
			sa.setDescription(menu.getDesc());
			sa.setCommand(menu.getCommand());
			sa.setIcon(menu.getIcon());
			sa.setCode(menu.getId());
			sa.setCategory(menu.getCategory());
			sa.setName(menu.getName());
			sa.setTarget(menu.getTarget());
			sa.setProvider(menu.getProvider());
			sa.setSwsId(parameters.getSwsId());
			sa.setPosition(menu.getPosition());
			em.persist(sa);
		}
		return new RemoteResult(200, "ok");
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "卸载指定的菜单", returnContentType = "text/json", returnUsage = "无返回")
	public RemoteResult unmountMenu(UnmountMenuParameters parameters)
			throws CircuitException {
		String jpql = "delete from SwsMenu a where a.swsId=:swsId and a.provider=:provider and a.code=:code";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsId());
		q.setParameter("provider", parameters.getProvider());
		q.setParameter("code", parameters.getCode());
		q.executeUpdate();
		return new RemoteResult(200, "ok");
	}
}
