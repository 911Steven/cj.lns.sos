package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsRole;
import cj.lns.common.sos.service.model.sws.SwsRoleContact;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "roleService")
public class RoleService implements IRoleService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean containsUser(BigInteger swsId, String roleCode,
			String userCode) {
		if ("publicToNet".equals(roleCode)) {// 固定角色，面向公网开放，因此无论是谁均包含其内。
			return true;
		}
		if ("publicToMember".equals(roleCode)) {// 固定角色，面向会员开放，因此只要是会员无论是谁均包含其内。
			String jpql = "select r from SosUser r where r.userCode=:userCode";
			Query q = em.createQuery(jpql);
			q.setParameter("userCode", userCode);
			try {
				q.getSingleResult();
				return true;
			} catch (NoResultException e) {
				return false;
			}
		}
		String jpql = "select r from SwsRole r,SwsContact c,SwsRoleContact rc "
				+ " where r.swsId=:swsId " + "and c.userCode=rc.userCode "
				+ " and r.roleCode=rc.roleCode " + "and rc.swsId=:swsId"
				+ " and r.roleCode=:roleCode " + "and c.userCode=:userCode ";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("roleCode", roleCode);
		q.setParameter("userCode", userCode);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}

	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public BigInteger addRole(BigInteger swsId, String roleCode,
			String roleName, String desc) {
		deleteRole(swsId, roleCode);
		SwsRole r = new SwsRole();
		r.setDescription(desc);
		r.setRoleCode(roleCode);
		r.setRoleName(roleName);
		r.setSwsId(swsId);
		em.persist(r);
		em.flush();
		return r.getId();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void deleteRole(BigInteger swsId, String roleCode) {

		String jpql = "delete from SwsRole r where r.swsId=:swsId and r.roleCode=:roleCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("roleCode", roleCode);
		q.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@CjTransaction(unitName = "sosdb")
	@Override
	public List<SwsRole> getRoles(BigInteger swsId) {
		String jpql = "select r from SwsRole r where r.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		return q.getResultList();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void addUsers(BigInteger swsId, String roleCode,
			List<String> userCodes) {
		String jpql = "select r from SwsRoleContact r where r.swsId=:swsId and r.roleCode=:roleCode and r.userCode in :userCodes";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("roleCode", roleCode);
		q.setParameter("userCodes", userCodes);
		List<?> list = q.getResultList();
		for (Object o : list) {
			SwsRoleContact c = (SwsRoleContact) o;
			if (userCodes.contains(c.getUserCode())) {
				userCodes.remove(c.getUserCode());
			}
		}
		for (String userCode : userCodes) {
			SwsRoleContact src = new SwsRoleContact();
			src.setRoleCode(roleCode);
			src.setUserCode(userCode);
			src.setSwsId(swsId);
			em.persist(src);
		}

	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void removeUsers(BigInteger swsId, String roleCode,
			List<String> userCodes) {
		String jpql = "delete from SwsRoleContact r where r.swsId=:swsId and r.roleCode=:roleCode and r.userCode in :userCodes";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("roleCode", roleCode);
		q.setParameter("userCodes", userCodes);
		q.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@CjTransaction(unitName = "sosdb")
	@Override
	public List<SwsContact> getContacts(BigInteger swsId, String roleCode) {
		String jpql = "select r from SwsRoleContact r where r.swsId=:swsId and r.roleCode=:roleCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("roleCode", roleCode);
		List<?> list = q.getResultList();
		if (list.isEmpty())
			return new ArrayList<SwsContact>();
		List<String> userCodes = new ArrayList<String>();
		for (Object o : list) {
			SwsRoleContact rc = (SwsRoleContact) o;
			userCodes.add(rc.getUserCode());
		}
		jpql = "select c from SwsContact c where c.swsId=:swsId and c.userCode in :users";
		q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("users", userCodes);
		return q.getResultList();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean isServicewsContacts(String userCode, BigInteger swsId) {
		String jpql = "select r from SwsContact r where r.swsId=:swsId and r.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("userCode", userCode);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean isServicewsOwner(String userCode, BigInteger swsId) {
		String jpql = "select r from SwsInfo r where r.id=:swsId and r.owner=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("userCode", userCode);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean isServiceosUser(String userCode) {
		String jpql = "select r from SosUser r where  r.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", userCode);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

}
