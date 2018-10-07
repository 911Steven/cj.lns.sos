package cj.lns.chip.sos.service.sws.user.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.SosUserInfo;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.lns.chip.sos.service.sws.BaseService;
import cj.lns.chip.sos.service.sws.ServicewsInfo;
import cj.lns.chip.sos.service.sws.remote.parameter.GetOwnerParameters;
import cj.lns.common.sos.service.model.SosProperty;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.model.sws.SwsPortalConf;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "swsOwnerService")
public class SwsOwnerService extends BaseService
		implements ISwsOwnerService, IEntityManagerable {
	EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public List<String> getAllSwsid(String userCode) {
		String jpql = "select p from SwsInfo p where p.owner=:owner";
		Query q = em.createQuery(jpql);
		q.setParameter("owner", userCode);
		List<?> swsList = q.getResultList();
		List<String> ret = new ArrayList<>();
		for (Object o : swsList) {
			SwsInfo si = (SwsInfo) o;
			String swsid = si.getId().toString();
			ret.add(swsid);
		}
		return ret;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public SosUserInfo getServicews(String userCode) throws CircuitException {
		String jpql = "select p from SosProperty p where p.propName='sos.id'";
		Query qsosp = em.createQuery(jpql);
		List<?> qsosList = qsosp.getResultList();
		String sosid = "";
		if (!qsosList.isEmpty()) {
			SosProperty soskv = (SosProperty) qsosList.get(0);
			sosid = soskv.getPropValue();
		}
		jpql = "select p from SosUser p where p.userCode=:userCode";
		Query quser = em.createQuery(jpql);
		quser.setParameter("userCode", userCode);
		List<?> users = quser.getResultList();
		if (users.isEmpty()) {
			throw new CircuitException("404",
					String.format("用户不存在。%s", userCode));
		}
		SosUser user = (SosUser) users.get(0);
		if (user.getDefaultSws().compareTo(BigInteger.valueOf(1L)) < 0) {
			throw new CircuitException("405", String.format("用户没有默认视窗。"));
		}
		jpql = "select p from SwsInfo p where p.id=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", user.getDefaultSws());
		List<?> swsList = q.getResultList();
		if (swsList.isEmpty()) {
			throw new CircuitException("404",
					String.format("用户:%s还没有服务视窗系统。", userCode));
		}
		SwsInfo sws = (SwsInfo) swsList.get(0);
		jpql = "select p from SwsPortalConf p where p.swsId=:swsId";
		Query qp = em.createQuery(jpql);
		qp.setParameter("swsId", sws.getId());
		SwsPortalConf portal = null;
		try {
			portal = (SwsPortalConf) qp.getSingleResult();
		} catch (Exception e) {
			throw new CircuitException("404", String.format(
					"用户:%s 的服务视窗系统缺少配置信息。swsId:%s", userCode, sws.getId()));
		}

		ServicewsInfo soi = new ServicewsInfo();
		SosUserInfo ui = new SosUserInfo();
		// 网盘信息
		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		INetDisk disk = db.getUserDisk(user.getUserCode());
		if (disk != null) {
			long capacity = -1;
			if (disk.info().containsAttr("capacity")) {
				capacity = (long) disk.info().attr("capacity");
			}
			double useSpace = disk.useSpace();
			double dataSize = disk.dataSize();
			long cubeCount = disk.cubeCount();
			ui.setCapacity(capacity);
			ui.setUseSpace(useSpace);
			ui.setCubeCount(cubeCount);
			ui.setDataSize(dataSize);
		}
		ui.setCreatetime(user.getCreatetime());
		ui.setHead(user.getHead());
		ui.setId(user.getId());
		ui.setNickName(user.getNickName());
		ui.setRealName(user.getRealName());
		ui.setStatus(user.getStatus());
		ui.setUserCode(user.getUserCode());
		
		fill(soi, sosid, sws, portal);
		
		ui.getSwsList().add(soi);
		return ui;
	}

	private void fill(ServicewsInfo soi, String sosid, SwsInfo sws,
			SwsPortalConf p) {
		soi.setSwsId(sws.getId());
		soi.setSosId(sosid);
		soi.setDescription(sws.getDescription());
		soi.setInheritId(sws.getInheritId());
		soi.setLevel(sws.getLevel());
		soi.setFaceImg(sws.getFaceImg());
		
		soi.setName(sws.getName());
		soi.setUsePortal(sws.getUsePortal());
		soi.setUseCanvas(p.getUseCanvas());
		soi.setUseSceneId(p.getUseSceneId());
		soi.setUseTheme(p.getUseTheme());
		soi.setOwner(sws.getOwner());
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public SosUserInfo getAllSws(String userCode) throws CircuitException {
		List<ServicewsInfo> list = new ArrayList<ServicewsInfo>();
		String jpql = "select p from SosProperty p where p.propName='sos.id'";
		Query qsosp = em.createQuery(jpql);
		List<?> qsosList = qsosp.getResultList();
		String sosid = "";
		if (!qsosList.isEmpty()) {
			SosProperty soskv = (SosProperty) qsosList.get(0);
			sosid = soskv.getPropValue();
		}
		jpql = "select p from SosUser p where p.userCode=:userCode";
		Query quser = em.createQuery(jpql);
		quser.setParameter("userCode", userCode);
		List<?> users = quser.getResultList();
		if (users.isEmpty()) {
			throw new CircuitException("404",
					String.format("用户不存在。%s", userCode));
		}
		SosUser user = (SosUser) users.get(0);
		SosUserInfo ui = new SosUserInfo();
		// 网盘信息
		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		INetDisk disk = db.getUserDisk(user.getUserCode());
		if (disk != null) {
			long capacity = -1;
			if (disk.info().containsAttr("capacity")) {
				Object v=disk.info().attr("capacity");
				if(v instanceof Double)
				capacity = (long) (double)v;
			}
			double useSpace = disk.useSpace();
			double dataSize = disk.dataSize();
			long cubeCount = disk.cubeCount();
			ui.setCapacity(capacity);
			ui.setUseSpace(useSpace);
			ui.setCubeCount(cubeCount);
			ui.setDataSize(dataSize);
			ui.setHomeCapacity(disk.home().config().getCapacity());
		}
		ui.setCreatetime(user.getCreatetime());
		ui.setHead(user.getHead());
		ui.setId(user.getId());
		ui.setNickName(user.getNickName());
		ui.setRealName(user.getRealName());
		ui.setStatus(user.getStatus());
		ui.setUserCode(user.getUserCode());

		jpql = "select p from SwsInfo p where p.owner=:owner";
		Query q = em.createQuery(jpql);
		q.setParameter("owner", userCode);
		List<?> swsList = q.getResultList();
		if (swsList.isEmpty()) {
			return ui;
		}
		for (Object o : swsList) {
			SwsInfo sws = (SwsInfo) o;
			jpql = "select p from SwsPortalConf p where p.swsId=:swsId";
			Query qp = em.createQuery(jpql);
			qp.setParameter("swsId", sws.getId());
			SwsPortalConf portal = null;
			try {
				portal = (SwsPortalConf) qp.getSingleResult();
			} catch (Exception e) {
				throw new CircuitException("406", String.format(
						"用户:%s 的服务视窗系统缺少配置信息。swsId:%s", userCode, sws.getId()));
			}

			ServicewsInfo soi = new ServicewsInfo();
			fill(soi, sosid, sws, portal);
			list.add(soi);
		}
		ui.setSwsList(list);
		return ui;

	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void setDefaultSws(String userCode, String swsid)
			throws CircuitException {

		String jpql = "select p from SosUser p where p.userCode=:userCode";
		Query quser = em.createQuery(jpql);
		quser.setParameter("userCode", userCode);
		List<?> users = quser.getResultList();
		if (users.isEmpty()) {
			throw new CircuitException("404",
					String.format("用户不存在。%s", userCode));
		}
		SosUser user = (SosUser) users.get(0);
		user.setDefaultSws(new BigInteger(swsid));
		em.merge(user);
//		em.refresh(user);
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public SosUserInfo getOwnerInfo(GetOwnerParameters p)
			throws CircuitException {
		String jpql = "select p from SwsInfo p where p.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", p.getSwsid());
		SwsInfo si = null;
		try {
			si = (SwsInfo) q.getSingleResult();
		} catch (NoResultException e) {
			throw new CircuitException("404", "视窗不存在：" + p.getSwsid());
		}
		jpql = "select p from SosUser p where p.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", si.getOwner());
		SosUser user = null;
		try {
			user = (SosUser) q.getSingleResult();
			SosUserInfo ui = new SosUserInfo();
			ui.setCreatetime(user.getCreatetime());
			ui.setHead(user.getHead());
			ui.setId(user.getId());
			ui.setNickName(user.getNickName());
			ui.setRealName(user.getRealName());
			ui.setSignatureText(user.getSignatureText());
			ui.setStatus(user.getStatus());
			ui.setUserCode(user.getUserCode());
			return ui;
		} catch (NoResultException e) {
			throw new CircuitException("404", "用户不存在：" + si.getOwner());
		}
	}

}
