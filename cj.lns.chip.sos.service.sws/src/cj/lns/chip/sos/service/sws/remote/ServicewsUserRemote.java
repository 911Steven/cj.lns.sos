package cj.lns.chip.sos.service.sws.remote;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.sws.remote.parameter.AddContactToGroupByUidParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.AddContatcParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.GetContactByCidParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.RemoveContactParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.RemoveUserByCidParameters;
import cj.lns.chip.sos.service.sws.remote.parameter.RenameContactMemoNameParameters;
import cj.lns.chip.sos.sws.security.ISecuritySubject;
import cj.lns.common.sos.service.model.SosUser;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsContactGroup;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;
import cj.ultimate.util.StringUtil;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "视窗系统的用户(联系人)管理服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/sws/user", isExoteric = true)
public class ServicewsUserRemote
		implements IRemoteService, IEntityManagerable, ISecuritySubject {
	EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "获取一个联系人，如果不存在则异常", returnContentType = "text/json", returnUsage = "返回用户")
	public RemoteResult getContact(GetContactByCidParameters parameters)
			throws CircuitException {

		String jpql = "select c from SwsContact c where c.swsId=:swsId and c.id=:id";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("id", parameters.getCid());
		try {
			Object o = q.getSingleResult();
			RemoteResult result = new RemoteResult(200, "ok");
			result.content().writeBytes(new Gson().toJson(o).getBytes());
			return result;
		} catch (NoResultException e) {
			RemoteResult result = new RemoteResult(502,
					String.format("联系人标识为：%s在当前视窗中：%s不存在", parameters.getCid(),
							parameters.getSwsid()));
			return result;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "当前视窗中加入一个用户", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult addUserToGroup(
			AddContactToGroupByUidParameters parameters)
			throws CircuitException {

		String jpql = "select c from SosUser c where c.id=:uid";
		Query q = em.createQuery(jpql);
		q.setParameter("uid", parameters.getUid());
		try {
			SosUser user = (SosUser) q.getSingleResult();
			SwsContactGroup g = null;
			if (parameters.getGid().longValue() < 0) {// 表示要加入到公共分组中
				jpql = "select g from SwsContactGroup g where g.swsId=:swsId and g.groupType='open'";
				q = em.createQuery(jpql);
				q.setParameter("swsId", parameters.getSwsid());
				try {
					g = (SwsContactGroup) q.getSingleResult();
				} catch (NoResultException e) {
					return new RemoteResult(404, "视窗中不存在公共分组");
				}
			}
			SwsContact c = new SwsContact();
			c.setHeadPic(user.getHead());
			c.setUserCode(user.getUserCode());
			c.setJoinTime(new Date());
			if (parameters.getGid().longValue() < 0) {
				c.setOwnerGroupId(g.getId());
			} else {
				c.setOwnerGroupId(parameters.getGid());
			}
			c.setSwsId(parameters.getSwsid());
			String name = user.getNickName();
			if (StringUtil.isEmpty(name)) {
				if (!StringUtil.isEmpty(user.getRealName())) {
					name = user.getRealName();
				}
			}
			c.setMemoName(StringUtil.isEmpty(parameters.getMemoName()) ? name
					: parameters.getMemoName());
			c.setPersonalSignature(user.getSignatureText());
			em.persist(c);
		} catch (NoResultException e2) {
			RemoteResult result = new RemoteResult(502,
					String.format("用户%s在当前服务操作系统中不存在", parameters.getUid()));
			return result;
		}

		RemoteResult result = new RemoteResult(200, "成功添加为联系人");
		return result;

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "当前视窗中加入一个用户", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult addUser(AddContatcParameters parameters)
			throws CircuitException {
		String jpql = "select c from SwsContact c where c.swsId=:swsId and c.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("userCode", parameters.getUserCode());
		try {
			q.getSingleResult();
			RemoteResult result = new RemoteResult(501,
					String.format("视窗%s的联系人中已存在联系人%s", parameters.getSwsid(),
							parameters.getUserCode()));
			return result;
		} catch (NoResultException e) {
			jpql = "select c from SosUser c where c.userCode=:userCode";
			q = em.createQuery(jpql);
			q.setParameter("userCode", parameters.getUserCode());
			try {
				SosUser user = (SosUser) q.getSingleResult();
				SwsContact c = new SwsContact();
				c.setHeadPic(user.getHead());
				c.setUserCode(user.getUserCode());
				c.setJoinTime(new Date());
				c.setOwnerGroupId(parameters.getGroupId());
				c.setSwsId(parameters.getSwsid());
				c.setMemoName(StringUtil.isEmpty(parameters.getMemoName())
						? user.getNickName()
						: parameters.getMemoName());
				c.setPersonalSignature(user.getSignatureText());
				em.persist(c);
			} catch (NoResultException e2) {
				RemoteResult result = new RemoteResult(502, String
						.format("用户%s在当前服务操作系统中不存在", parameters.getUserCode()));
				return result;
			}

		}
		RemoteResult result = new RemoteResult(200, "成功添加联系人");
		return result;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "当前视窗中移除一个联系人", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult removeUser(RemoveContactParameters parameters)
			throws CircuitException {
		String jpql = "delete from SwsContact c where c.userCode=:userCode and c.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getUserCode());
		q.setParameter("swsId", parameters.getSwsid());
		int i = q.executeUpdate();
		if (i == 1) {
			RemoteResult result = new RemoteResult(200, "成功移除联系人");
			return result;
		} else {
			RemoteResult result = new RemoteResult(503,
					String.format("联系人：%s在视窗：%s不存在", parameters.getUserCode(),
							parameters.getSwsid()));
			return result;
		}

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "当前视窗中移除一个联系人", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult removeUserByCid(RemoveUserByCidParameters parameters)
			throws CircuitException {
		String jpql = "delete from SwsContact c where c.id=:cid and c.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("cid", parameters.getCid());
		q.setParameter("swsId", parameters.getSwsid());
		int i = q.executeUpdate();
		if (i == 1) {
			RemoteResult result = new RemoteResult(200, "成功移除联系人");
			return result;
		} else {
			RemoteResult result = new RemoteResult(503,
					String.format("联系人：%s在视窗：%s不存在", parameters.getCid(),
							parameters.getSwsid()));
			return result;
		}

	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "当前视窗中为联系人变更备注名", returnContentType = "text/json", returnUsage = "返回用户默认视窗")
	public RemoteResult renameMemoName(
			RenameContactMemoNameParameters parameters)
			throws CircuitException {
		String jpql = "update SwsContact c set c.memoName=:memoName where c.userCode=:userCode and c.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", parameters.getUserCode());
		q.setParameter("swsId", parameters.getSwsid());
		q.setParameter("memoName", parameters.getMemoName());
		int i = q.executeUpdate();
		if (i == 1) {
			RemoteResult result = new RemoteResult(200, "成功修改联系人备注名");
			return result;
		} else {
			RemoteResult result = new RemoteResult(503,
					String.format("联系人：%s在视窗：%s不存在", parameters.getUserCode(),
							parameters.getSwsid()));
			return result;
		}
	}

	@Override
	public String subjectName() {
		return "contact";
	}

	@Override
	public boolean containsUser(BigInteger swsId, String subjectInst,
			String userCode) {
		return userCode.equals(subjectInst);
	}
}
