package cj.lns.chip.sos.service.user.remote;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.user.remote.parameter.AccountParameters;
import cj.lns.chip.sos.website.framework.Face;
import cj.lns.common.sos.service.model.SosRole;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "认证服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/public/auth/", isExoteric = true)
public class AuthRemote implements IRemoteService, IEntityManagerable {
	EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "用户自定义代码认证", returnContentType = "text/json", returnUsage = "成功后返回默认视窗号，及是否有默认视窗")
	public RemoteResult authUserCode(AccountParameters parameters)
			throws CircuitException {
		String jpql = "select count(u) from SosUser u where u.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getAccount());
		long count = (long) q.getSingleResult();
		if (count < 1) {
			return new RemoteResult(404, "user is not found.");
		}
		jpql = "select u from SosUser u,SosAuth a where u.userCode=a.account and a.data=:password and u.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getAccount());
		q.setParameter("password", parameters.getPassword());
		try {
			SosUser u = (SosUser) q.getSingleResult();
			em.refresh(u);
			Face face=new Face(u.getNickName(),u.getHead(),u.getSignatureText(),u.getBriefing(),u.getSex());
			// 取SOS角色
			jpql = "select r from SosRole r,SosRoleUa u where r.code=u.roleCode and u.userCode=:userCode";
			q = em.createQuery(jpql);
			q.setParameter("userCode", parameters.getAccount());
			@SuppressWarnings("unchecked")
			List<SosRole> roles = q.getResultList();
			RemoteResult result = new RemoteResult(200, "认证成功");
			boolean hasDefaultSwsId = u.getDefaultSws() == null ? false : true;
			result.head("hasDefaultSwsId", String.valueOf(hasDefaultSwsId));
			result.head("account", parameters.getAccount());
			result.head("face",new Gson().toJson(face));
			if (hasDefaultSwsId)
				result.head("defaultSwsId", u.getDefaultSws().toString());
			String json = new Gson().toJson(roles);
			result.content().writeBytes(json.getBytes());
			return result;
		} catch (NoResultException e) {
			return new RemoteResult(600, "be not authenticated");
		}

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "视窗号认证", returnContentType = "text/json", returnUsage = "返回状态:200成功,否则报错")
	public RemoteResult authServicewsId(AccountParameters parameters)
			throws CircuitException {
		BigInteger swsid = new BigInteger(parameters.getAccount());
		String jpql = "select count(s) from SwsInfo s where s.id=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", swsid);
		long count = (long) q.getSingleResult();
		if (count < 1) {
			return new RemoteResult(404, "servicews is not found.");
		}

		jpql = "select u from SwsInfo s, SosUser u,SosAuth a where s.owner=u.userCode and u.userCode=a.account and a.data=:password and s.id=:swsid";
		q = em.createQuery(jpql);
		q.setParameter("swsid", swsid);
		q.setParameter("password", parameters.getPassword());
		SosUser u = null;
		try {
			u = (SosUser) q.getSingleResult();
			em.refresh(u);
		} catch (NoResultException e) {
			return new RemoteResult(600, "be not authenticated");
		}
		Face face=new Face(u.getNickName(),u.getHead(),u.getSignatureText(),u.getBriefing(),u.getSex());
		RemoteResult result = new RemoteResult(200, "认证成功");
		// 取SOS角色
		jpql = "select r from SosRole r,SosRoleUa u where r.code=u.roleCode and u.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("userCode", u.getUserCode());
		@SuppressWarnings("unchecked")
		List<SosRole> roles = q.getResultList();
		boolean hasDefaultSwsId = u.getDefaultSws() == null ? false : true;
		result.head("hasDefaultSwsId", String.valueOf(hasDefaultSwsId));
		result.head("account", u.getUserCode());
		result.head("face",new Gson().toJson(face));
		if (hasDefaultSwsId)
			result.head("defaultSwsId", u.getDefaultSws().toString());
		String json = new Gson().toJson(roles);
		result.content().writeBytes(json.getBytes());
		return result;
	}
}
