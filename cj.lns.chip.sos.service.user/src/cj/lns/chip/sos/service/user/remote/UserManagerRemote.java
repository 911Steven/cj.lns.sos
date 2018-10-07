package cj.lns.chip.sos.service.user.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.cube.framework.TupleDocument;
import cj.lns.chip.sos.disk.INetDisk;
import cj.lns.chip.sos.service.db.IDatabaseCloud;
import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.IServiceosServiceModule;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.user.remote.parameter.FindUserResourcesParameters;
import cj.lns.chip.sos.service.user.remote.parameter.GetUserFaceParameters;
import cj.lns.chip.sos.service.user.remote.parameter.SosRoleUserParameters;
import cj.lns.common.sos.service.model.SosRoleUa;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsInfo;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.security.RSAUtils;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "用户管理服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/user/manager", isExoteric = true)
public class UserManagerRemote implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "从用户中删除一个系统角色", returnContentType = "text/json", returnUsage = "内容：json face")
	public RemoteResult removeSosRoleFromUser(SosRoleUserParameters p)
			throws CircuitException {
		String jqpl = "delete from SosRoleUa u where u.userCode=:userCode and u.roleCode=:roleCode";
		Query q = em.createQuery(jqpl);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("roleCode", p.getRoleCode());
		q.executeUpdate();
		return new RemoteResult(200, "ok");
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "添加一个系统角色到用户", returnContentType = "text/json", returnUsage = "内容：json face")
	public RemoteResult addSosRoleToUser(SosRoleUserParameters p)
			throws CircuitException {
		String jqpl = "select u from SosRoleUa u where u.userCode=:userCode and u.roleCode=:roleCode";
		Query q = em.createQuery(jqpl);
		q.setParameter("userCode", p.getUserCode());
		q.setParameter("roleCode", p.getRoleCode());
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SosRoleUa ua = new SosRoleUa();
			ua.setRoleCode(p.getRoleCode());
			ua.setUserCode(p.getUserCode());
			em.persist(ua);
		}
		return new RemoteResult(200, "ok");
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "删除用户实体及其网盘", returnContentType = "text/json", returnUsage = "内容：json face")
	public RemoteResult removeUser(FindUserResourcesParameters p)
			throws CircuitException {
		String jqpl = "delete from SosUser u where u.userCode=:userCode";
		Query q = em.createQuery(jqpl);
		q.setParameter("userCode", p.getUserCode());
		q.executeUpdate();

		jqpl = "delete from SosAuth u where u.account=:userCode";
		q = em.createQuery(jqpl);
		q.setParameter("userCode", p.getUserCode());
		q.executeUpdate();

		jqpl = "delete from SosRoleUa u where u.userCode=:userCode";
		q = em.createQuery(jqpl);
		q.setParameter("userCode", p.getUserCode());
		q.executeUpdate();

		IServiceosServiceModule m = ServiceosServiceModule.get();
		IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
		if (db.existsUserDisk(p.getUserCode())) {
			INetDisk disk = db.getUserDisk(p.getUserCode());
			disk.delete();
		}
		db.getLnsSysHome().deleteDocOne("userKeyTools",String.format("{'tuple.user':'%s'}",p.getUserCode()));
		return new RemoteResult(200, "ok");
	}
	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询用户资源", returnContentType = "text/json", returnUsage = "内容：json face")
	public RemoteResult findUserResources(FindUserResourcesParameters p)
			throws CircuitException {
		String jqpl = "select u from SosUser u where u.userCode=:userCode";
		Query q = em.createQuery(jqpl);
		q.setParameter("userCode", p.getUserCode());
		SosUser user = null;
		try {
			user = (SosUser) q.getSingleResult();
			
			jqpl = "select si from SwsInfo si where si.owner=:userCode";
			q = em.createQuery(jqpl);
			q.setParameter("userCode", p.getUserCode());
			List<SwsInfo> info = q.getResultList();

			jqpl = "select si from SosRoleUa si where si.userCode=:userCode";
			q = em.createQuery(jqpl);
			q.setParameter("userCode", p.getUserCode());
			List<SosRoleUa> ua=q.getResultList();
			
			RemoteResult rr = new RemoteResult(200, "ok");
			Map<String, Object> map = new HashMap<>();
			map.put("user", user);
			map.put("roles", ua);
			map.put("swsList", info);

			IServiceosServiceModule m = ServiceosServiceModule.get();
			IDatabaseCloud db = (IDatabaseCloud) m.site().databaseCloud();
			if (db.existsUserDisk(p.getUserCode())) {
				INetDisk disk = db.getUserDisk(p.getUserCode());
				Map<String, Object> dmap = new HashMap<>();
				Object capacity = disk.info().attr("capacity");
				if (!disk.info().containsAttr("capacity")) {
					capacity = 0;
				}
				dmap.put("capacity", capacity);
				dmap.put("cubeCount", disk.cubeCount());
				dmap.put("useSpace", disk.useSpace());
				dmap.put("dataSize", disk.dataSize());
				map.put("disk", dmap);
			}
			rr.content().writeBytes(new Gson().toJson(map).getBytes());
			return rr;
		} catch (NoResultException e) {
			RemoteResult rr = new RemoteResult(404, "用户不存在");
			rr.content().writeBytes("{}".getBytes());
			return rr;
		}
	}

}
