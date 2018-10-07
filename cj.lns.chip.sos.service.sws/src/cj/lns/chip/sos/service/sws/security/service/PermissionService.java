package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.sws.security.ServicewsAuthMethod;
import cj.lns.chip.sos.service.sws.security.remote.AllowGroupParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.AllowRoleParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DenyGroupParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetContactsParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.GetObjectAclsParameters;
import cj.lns.chip.sos.sws.security.Ace;
import cj.lns.chip.sos.sws.security.Acl;
import cj.lns.chip.sos.sws.security.ISecuritySubject;
import cj.lns.common.sos.service.model.sws.SwsContact;
import cj.lns.common.sos.service.model.sws.SwsPa;
import cj.lns.common.sos.service.model.sws.SwsPd;
import cj.lns.common.sos.service.model.sws.SwsPermission;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.IServiceSite;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "permissionService")
public class PermissionService
		implements IPermissionService, IEntityManagerable {
	private EntityManager em;
	@CjServiceRef(refByName = IServiceSite.KEY_SERVICE_SITE)
	private IServiceSite site;
	@CjServiceRef(refByName = "roleService")
	private IRoleService role;
	private Map<String, ISecuritySubject> subjects;

	public PermissionService() {
	}

	@SuppressWarnings("unchecked")
	protected Map<String, ISecuritySubject> subjects() {
		if (subjects == null) {
			subjects = (Map<String, ISecuritySubject>) site
					.getService("subjects");
		}
		return subjects;
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public Acl getObjectAcl(BigInteger swsid, String objectName,
			String objectInst, String permissionCode) {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId and p.objectName=:objectName and p.objectInst=:objectInst and p.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsid);
		q.setParameter("objectName", objectName);
		q.setParameter("objectInst", objectInst);
		q.setParameter("permissionCode", permissionCode);
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		Acl acl = new Acl();
		acl.setResourceId(perm.getObjectName());
		acl.setValueId(perm.getObjectInst());
		acl.setAuthMethod(perm.getAuthMethod());
		acl.setPermissionCode(perm.getPermissionCode());
		acl.setPermissionName(perm.getPermissionName());
		acl.setSwsid(perm.getSwsId());
		acl.setPermissionPhyId(perm.getId());

		jpql = "select pa from SwsPa pa where pa.permissionId = :permissionId";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		List<?> pa = q.getResultList();
		for (Object o : pa) {
			SwsPa a = (SwsPa) o;
			acl.addAllowSubject(a.getSubjectName(), a.getSubjectInst());
		}

		jpql = "select pd from SwsPd pd where pd.permissionId = :permissionId";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		List<?> pd = q.getResultList();
		for (Object o : pd) {
			SwsPd d = (SwsPd) o;
			acl.addDenyUser(d.getUserCode());
		}
		return acl;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void addPermission(SwsPermission p) throws CircuitException {
		if (existsPermission(p.getSwsId(), p.getObjectName(), p.getObjectInst(),
				p.getPermissionCode())) {
			throw new CircuitException("500", "许可已存在。");
		}
		em.persist(p);
	}

	private boolean existsPermission(BigInteger swsId, String objectName,
			String objectInst, String permissionCode) {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId and p.objectName=:objectName and p.objectInst=:objectInst and p.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("objectName", objectName);
		q.setParameter("objectInst", objectInst);
		q.setParameter("permissionCode", permissionCode);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@SuppressWarnings("unchecked")
	@Override
	public List<SwsPermission> getUserPermissions(BigInteger swsid,
			String userCode, String objectName, String objectInst)
					throws CircuitException {
		String jpql = "select p from SwsPermission p where p.swsId=:swsid and p.objectName=:objectName and "
				+ "p.objectInst=:objectInst";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", swsid);
		q.setParameter("objectName", objectName);
		q.setParameter("objectInst", objectInst);
		List<?> permList = q.getResultList();
		if (permList.isEmpty()) {
			return (List<SwsPermission>) permList;
		}

		List<BigInteger> pids = new ArrayList<BigInteger>();
		for (Object o : permList) {
			SwsPermission sp = (SwsPermission) o;
			pids.add(sp.getId());
		}
		jpql = "select pa from SwsPa pa where pa.permissionId in :permissionIds";
		q = em.createQuery(jpql);
		q.setParameter("permissionIds", pids);
		List<?> paList = q.getResultList();
		if (paList.isEmpty()) {
			permList.clear();
			return (List<SwsPermission>) permList;
		}
		List<BigInteger> allows = new ArrayList<BigInteger>();// 许可标识
		for (Object o : paList) {
			SwsPa pa = (SwsPa) o;

			ISecuritySubject sub = subjects().get(pa.getSubjectName());
			if (sub == null) {
				throw new CircuitException("404",
						String.format("主题%s未被定义", pa.getSubjectName()));
			}
			if (sub.containsUser(swsid, pa.getSubjectInst(), userCode)) {
				if (!allows.contains(pa.getPermissionId())) {
					allows.add(pa.getPermissionId());
				}
			}
		}

		jpql = "select pd from SwsPd pd where pd.permissionId in :permissionIds and pd.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("permissionIds", pids);
		q.setParameter("userCode", userCode);
		List<?> pdList = q.getResultList();

		List<BigInteger> denies = new ArrayList<BigInteger>();// 拒绝列表，许可标识
		for (Object o : pdList) {
			SwsPd pd = (SwsPd) o;
			if (!denies.contains(pd.getPermissionId())) {
				denies.add(pd.getPermissionId());
			}
		}
		allows.removeAll(denies);// 余下充许的
		List<SwsPermission> retList = new ArrayList<SwsPermission>();
		for (Object op : permList) {
			SwsPermission sp = (SwsPermission) op;
			if (allows.contains(sp.getId())) {
				retList.add(sp);
			}
		}
		return retList;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public SwsPermission getUserPermissions(BigInteger swsid, String userCode,
			String objectName, String objectInst, String permissionCode)
					throws CircuitException {

		String jpql = "select p from SwsPermission p where p.swsId=:swsid and p.objectName=:objectName and "
				+ "p.objectInst=:objectInst and p.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", swsid);
		q.setParameter("objectName", objectName);
		q.setParameter("objectInst", objectInst);
		q.setParameter("permissionCode", permissionCode);
		List<?> permList = q.getResultList();
		if (permList.isEmpty()) {
			return null;
		}

		List<BigInteger> pids = new ArrayList<BigInteger>();
		for (Object o : permList) {
			SwsPermission sp = (SwsPermission) o;
			pids.add(sp.getId());
		}
		jpql = "select pa from SwsPa pa where pa.permissionId in :permissionIds";
		q = em.createQuery(jpql);
		q.setParameter("permissionIds", pids);
		List<?> paList = q.getResultList();
		if (paList.isEmpty()) {
			permList.clear();
			return null;
		}
		List<BigInteger> allows = new ArrayList<BigInteger>();// 许可标识
		for (Object o : paList) {
			SwsPa pa = (SwsPa) o;
			ISecuritySubject sub = subjects().get(pa.getSubjectName());
			if (sub == null) {
				throw new CircuitException("404",
						String.format("主题%s未被定义", pa.getSubjectName()));
			}
			if (sub.containsUser(swsid, pa.getSubjectInst(), userCode)) {
				if (!allows.contains(pa.getPermissionId())) {// 去重复，为什么？因为角色a对资源r拥有p许可，而同时用户u属于a，而u也拥有p许可
					allows.add(pa.getPermissionId());
				}
			}

		}

		jpql = "select pd from SwsPd pd where pd.permissionId in :permissionIds and pd.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("permissionIds", pids);
		q.setParameter("userCode", userCode);
		List<?> pdList = q.getResultList();

		List<BigInteger> denies = new ArrayList<BigInteger>();// 拒绝列表，许可标识
		for (Object o : pdList) {
			SwsPd pd = (SwsPd) o;
			if (!denies.contains(pd.getPermissionId())) {
				denies.add(pd.getPermissionId());
			}
		}
		allows.removeAll(denies);// 余下充许的
		SwsPermission ret = null;
		for (Object op : permList) {
			SwsPermission sp = (SwsPermission) op;
			if (allows.contains(sp.getId())) {
				ret = sp;
				break;
			}
		}
		return ret;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void removePermission(BigInteger permissionId)
			throws CircuitException {
		String jpql = "delete from SwsPermission p where p.id=:id";
		Query q = em.createQuery(jpql);
		q.setParameter("id", permissionId);
		q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void clearPermission(BigInteger permissionId)
			throws CircuitException {
		String jpql = "delete from SwsPa p where p.permissionId=:id";
		Query q = em.createQuery(jpql);
		q.setParameter("id", permissionId);
		q.executeUpdate();
		jpql = "delete from SwsPd p where p.permissionId=:id";
		q = em.createQuery(jpql);
		q.setParameter("id", permissionId);
		q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public SwsPermission find(BigInteger swsId, String objectName,
			String objectInst, String permissionCode) throws CircuitException {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId and p.objectName=:objectName and p.objectInst=:objectInst and p.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("swsId", swsId);
		q.setParameter("objectName", objectName);
		q.setParameter("objectInst", objectInst);
		q.setParameter("permissionCode", permissionCode);
		SwsPermission p = null;
		try {
			p = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
		}
		return p;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void allow(BigInteger permissionId, String subjectName,
			String subjectInst) throws CircuitException {
		if (!subjects().containsKey(subjectName)) {
			throw new CircuitException("404",
					String.format("主体%s未被定义", subjectName));
		}
		String jpql = "select p from SwsPa p where p.permissionId=:permissionId and p.subjectName=:subjectName and p.subjectInst=:subjectInst";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("subjectName", subjectName);
		q.setParameter("subjectInst", subjectInst);
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsPa pa = new SwsPa();
			pa.setPermissionId(permissionId);
			pa.setSubjectInst(subjectInst);
			pa.setSubjectName(subjectName);
			em.persist(pa);
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean isAllow(BigInteger permissionId, String subjectName,
			String subjectInst) throws CircuitException {
		String jpql = "select p from SwsPa p where p.permissionId=:permissionId and p.subjectName=:subjectName and p.subjectInst=:subjectInst";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("subjectName", subjectName);
		q.setParameter("subjectInst", subjectInst);
		try {
			q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public int removeAllow(BigInteger permissionId, String subjectName,
			String subjectInst) throws CircuitException {
		String jpql = "delete from SwsPa p where p.permissionId=:permissionId and p.subjectName=:subjectName and p.subjectInst=:subjectInst";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("subjectName", subjectName);
		q.setParameter("subjectInst", subjectInst);
		return q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public int clearAllows(BigInteger permissionId) throws CircuitException {
		String jpql = "delete from SwsPa p where p.permissionId=:permissionId ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		return q.executeUpdate();

	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void deny(BigInteger permissionId, String userCode)
			throws CircuitException {
		String jpql = "select p from SwsPd p where p.permissionId=:permissionId and p.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("userCode", userCode);
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsPd pd = new SwsPd();
			pd.setPermissionId(permissionId);
			pd.setUserCode(userCode);
			em.persist(pd);
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean isDeny(BigInteger permissionId, String userCode)
			throws CircuitException {
		String jpql = "select p from SwsPd p where p.permissionId=:permissionId and p.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
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
	public void clearDenies(BigInteger permissionId) throws CircuitException {
		String jpql = "select p from SwsPd p where p.permissionId=:permissionId";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void removeDeny(BigInteger permissionId, String userCode)
			throws CircuitException {
		String jpql = "delete from SwsPd p where p.permissionId=:permissionId and p.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("userCode", userCode);
		q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@SuppressWarnings("unchecked")
	@Override
	public List<SwsPa> getAllows(BigInteger permissionId)
			throws CircuitException {
		String jpql = "select p from SwsPa p where p.permissionId=:permissionId";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		List<?> list = q.getResultList();
		return (List<SwsPa>) list;
	}

	@CjTransaction(unitName = "sosdb")
	@SuppressWarnings("unchecked")
	@Override
	public List<SwsPd> getDenies(BigInteger permissionId)
			throws CircuitException {
		String jpql = "select p from SwsPd p where p.permissionId=:permissionId";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		List<?> list = q.getResultList();
		return (List<SwsPd>) list;
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void setAuthMethod(BigInteger permissionId,
			ServicewsAuthMethod method) throws CircuitException {
		String jpql = "update from SwsPermission p set p.authMethod=:authMethod where p.id=:permissionId";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("authMethod", method);
		q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void allowRole(AllowRoleParameters p) {
		/*
		 * ~ 如果许可不存在则新建，存在则挂在其下
		 * ~ 充许一个角色，则清空充许表而后插入该角色，因为角色与所有其它主体互斥
		 */
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			perm = new SwsPermission();
			perm.setAuthMethod((byte) -1);
			perm.setObjectInst(p.getObjectInst());
			perm.setObjectName(p.getObjectName());
			perm.setPermissionCode(p.getPermissionCode());
			perm.setPermissionName(p.getPermissionName());
			perm.setSwsId(p.getSwsId());
			em.persist(perm);
			em.flush();
		}
		jpql = "delete from SwsPa p where p.permissionId=:permissionId";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		q.executeUpdate();

		SwsPa pa = new SwsPa();
		pa.setPermissionId(perm.getId());
		pa.setSubjectInst(p.getRoleCode());
		pa.setSubjectName("role");
		em.persist(pa);
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void denyGroup(DenyGroupParameters p) {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			return;
		}
		jpql = "delete from SwsPa p where p.permissionId=:permissionId and p.subjectName='contactGroup' and p.subjectInst=:subjectInst";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		q.setParameter("subjectInst", p.getGroupId().toString());
		q.executeUpdate();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void allowGroup(AllowGroupParameters p) {
		/*
		 * ~ 如果许可不存在则新建，存在则挂在其下
		 * ~ 充许组即是自定义，充许表中的角色清除
		 */
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			perm = new SwsPermission();
			perm.setAuthMethod((byte) -1);
			perm.setObjectInst(p.getObjectInst());
			perm.setObjectName(p.getObjectName());
			perm.setPermissionCode(p.getPermissionCode());
			perm.setPermissionName(p.getPermissionName());
			perm.setSwsId(p.getSwsId());
			em.persist(perm);
			em.flush();
		}
		jpql = "delete from SwsPa p where p.permissionId=:permissionId and p.subjectName='role'";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		q.executeUpdate();

		jpql = "select p from SwsPa p where p.permissionId=:permissionId and p.subjectName='contactGroup' and p.subjectInst=:groupId";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		q.setParameter("groupId", p.getGroupId().toString());
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsPa pa = new SwsPa();
			pa.setPermissionId(perm.getId());
			pa.setSubjectInst(p.getGroupId().toString());
			pa.setSubjectName("contactGroup");
			em.persist(pa);
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void allowContacts(AllowContactsParameters p) {
		/*
		 * ~ 如果许可不存在则新建，存在则挂在其下
		 * ~ 充许组即是自定义，充许表中的角色清除
		 */
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			perm = new SwsPermission();
			perm.setAuthMethod((byte) -1);
			perm.setObjectInst(p.getObjectInst());
			perm.setObjectName(p.getObjectName());
			perm.setPermissionCode(p.getPermissionCode());
			perm.setPermissionName(p.getPermissionName());
			perm.setSwsId(p.getSwsId());
			em.persist(perm);
			em.flush();
		}
		jpql = "delete from SwsPa p where p.permissionId=:permissionId and ( p.subjectName='role' or p.subjectName='contact' )";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		q.executeUpdate();

		String[] users = p.getUserCodes().split(",");
		for (String u : users) {
			SwsPa a = new SwsPa();
			a.setSubjectInst(u);
			a.setSubjectName("contact");
			a.setPermissionId(perm.getId());
			em.persist(a);
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void denyContacts(DenyContactsParameters p) {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			return;
		}
		jpql = "delete from SwsPd p where p.permissionId=:permissionId  ";
		q = em.createQuery(jpql);
		q.setParameter("permissionId", perm.getId());
		q.executeUpdate();

		String[] users = p.getUserCodes().split(",");
		for (String u : users) {
			SwsPd d = new SwsPd();
			d.setUserCode(u);
			d.setPermissionId(perm.getId());
			em.persist(d);
		}
	}

	@SuppressWarnings("unchecked")
	@CjTransaction(unitName = "sosdb")
	@Override
	public List<SwsContact> getAllowContacts(GetContactsParameters p) {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			return new ArrayList<>();
		}
		jpql = "select c from SwsPa p,SwsContact c where p.subjectName='contact' and p.subjectInst=c.userCode and p.permissionId=:permId";
		q = em.createQuery(jpql);
		q.setParameter("permId", perm.getId());
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@CjTransaction(unitName = "sosdb")
	@Override
	public List<SwsContact> getDenyContacts(GetContactsParameters p) {
		String jpql = "select p from SwsPermission p where p.swsId=:swsId  and "
				+ " p.permissionCode=:permissionCode and "
				+ " p.objectInst=:objectInst and "
				+ " p.objectName=:objectName ";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm = null;
		try {
			perm = (SwsPermission) q.getSingleResult();
		} catch (NoResultException e) {
			return new ArrayList<>();
		}
		jpql = "select c from SwsPd p,SwsContact c where p.userCode=c.userCode and p.permissionId=:permId";
		q = em.createQuery(jpql);
		q.setParameter("permId", perm.getId());
		return q.getResultList();
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public List<Acl> getObjectAcls(GetObjectAclsParameters p)
			throws CircuitException {

		String jpql = "select p from SwsPermission p where p.swsId=:swsid and p.objectName=:objectName ";
		boolean useIn = false;
		if (p.getPermissionCode().contains(",")) {// 使用IN语句
			jpql = String.format("%s and  p.permissionCode in :permissionCode",
					jpql);
			useIn = true;
		} else {
			jpql = String.format("%s and  p.permissionCode = :permissionCode",
					jpql);
		}
		Query q = em.createQuery(jpql);
		q.setParameter("swsid", p.getSwsId());
		q.setParameter("objectName", p.getObjectName());
		if (useIn) {
			String[] arr=p.getPermissionCode().split(",");
			List<String> list=new ArrayList<>();
			for(String code:arr){
				list.add(code);
			}
			q.setParameter("permissionCode", list);
		} else {
			q.setParameter("permissionCode", p.getPermissionCode());
		}
		List<?> permList = q.getResultList();
		if (permList.isEmpty()) {
			return new ArrayList<>();
		}

		List<BigInteger> pids = new ArrayList<BigInteger>();
		for (Object o : permList) {
			SwsPermission sp = (SwsPermission) o;
			pids.add(sp.getId());
		}
		jpql = "select pa from SwsPa pa where pa.permissionId in :permissionIds";
		q = em.createQuery(jpql);
		q.setParameter("permissionIds", pids);
		List<?> paList = q.getResultList();
		if (paList.isEmpty()) {
			permList.clear();
			return new ArrayList<>();
		}
		List<BigInteger> allows = new ArrayList<BigInteger>();// 许可标识
		Map<BigInteger,List<Ace>> allowAces=new HashMap<>();
		for (Object o : paList) {
			SwsPa pa = (SwsPa) o;
			ISecuritySubject sub = subjects().get(pa.getSubjectName());
			if (sub == null) {
				throw new CircuitException("404",
						String.format("主体对象%s未被定义", pa.getSubjectName()));
			}
			if (sub.containsUser(p.getSwsId(), pa.getSubjectInst(),
					p.getPrincipal())) {
				if (!allows.contains(pa.getPermissionId())) {// 去重复，为什么？因为角色a对资源r拥有p许可，而同时用户u属于a，而u也拥有p许可
					allows.add(pa.getPermissionId());
					Ace e=new Ace(pa.getSubjectName(), pa.getSubjectInst());
					List<Ace> aces=allowAces.get(pa.getPermissionId());
					if(aces==null){
						aces=new ArrayList<>();
						allowAces.put(pa.getPermissionId(), aces);
					}
					aces.add(e);
				}
			}

		}

		jpql = "select pd from SwsPd pd where pd.permissionId in :permissionIds and pd.userCode=:userCode";
		q = em.createQuery(jpql);
		q.setParameter("permissionIds", pids);
		q.setParameter("userCode", p.getPrincipal());
		List<?> pdList = q.getResultList();

		List<BigInteger> denies = new ArrayList<BigInteger>();// 拒绝列表，许可标识
		for (Object o : pdList) {
			SwsPd pd = (SwsPd) o;
			if (!denies.contains(pd.getPermissionId())) {
				denies.add(pd.getPermissionId());
			}
		}
		allows.removeAll(denies);// 余下充许的
		List<Acl> acls = new ArrayList<>();
		for (Object op : permList) {
			SwsPermission sp = (SwsPermission) op;
			if (allows.contains(sp.getId())) {
				Acl acl =null;
				List<Ace> aces=allowAces.get(sp.getId());
				if(aces!=null){
					acl=new Acl(aces);
				}else{
					acl= new Acl();
				}
				acls.add(acl);
				acl.setAuthMethod(sp.getAuthMethod());
				acl.setPermissionCode(sp.getPermissionCode());
				acl.setPermissionName(sp.getPermissionName());
				acl.setPermissionPhyId(sp.getId());
				acl.setResourceId(sp.getObjectName());
				acl.setSwsid(sp.getSwsId());
				
				acl.setValueId(sp.getObjectInst());
			}
		}
		return acls;
	}

}
