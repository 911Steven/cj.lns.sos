package cj.lns.chip.sos.service.sws.security.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.sws.security.remote.parameter.CreateQuestionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.DelQuestionParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.PermissionCodeParameters;
import cj.lns.chip.sos.service.sws.security.remote.parameter.UpdateQuestionParameters;
import cj.lns.common.sos.service.model.sws.SwsAuthAsk;
import cj.lns.common.sos.service.model.sws.SwsPermission;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "authAnswerService")
public class AuthAnswerService
		implements IAuthAnswerService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public void addAuthAsk(BigInteger permissionId, BigInteger swsid,
			String ask, String answer) throws CircuitException {
		String jpql = "select q from SwsAuthAsk q where q.permissionId=:permissionId and q.ask=:ask and q.answer=:answer and q.swsId=:swsId";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("ask", ask);
		q.setParameter("answer", answer);
		q.setParameter("swsId", swsid);
		try {
			q.getSingleResult();
		} catch (NoResultException e) {
			SwsAuthAsk sq = new SwsAuthAsk();
			sq.setAnswer(answer);
			sq.setAsk(ask);
			sq.setPermissionId(permissionId);
			em.persist(sq);
		}
	}

	@CjTransaction(unitName = "sosdb")
	@Override
	public void removeAuthAsk(BigInteger questid) throws CircuitException {
		String jpql = "delete from SwsAuthAsk q where q.id=:questid";
		Query q = em.createQuery(jpql);
		q.setParameter("questid", questid);
		q.executeUpdate();
	}
	@SuppressWarnings("unchecked")
	@CjTransaction(unitName = "sosdb")
	@Override
	public List<SwsAuthAsk> getAuthAsk(BigInteger permissionId,
			BigInteger swsid) throws CircuitException {
		String jpql = "select q from SwsAuthAsk q where q.permissionId=:permissionId and q.swsId=:swsid";
		Query q = em.createQuery(jpql);
		q.setParameter("permissionId", permissionId);
		q.setParameter("swsid", swsid);
		return q.getResultList();
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public boolean auth(BigInteger askId, String answer) {
		String jpql = "select q from SwsAuthAsk q where q.id=:askId and q.answer=:answer";
		Query q = em.createQuery(jpql);
		q.setParameter("askId", askId);
		q.setParameter("answer", answer);
		try{
			q.getSingleResult();
			return true;
		}catch(NoResultException e){
			return false;
		}
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public List<SwsAuthAsk> getAuthAsks(PermissionCodeParameters p) throws CircuitException {
		String jpql = "select q from SwsPermission q where q.objectName=:objectName"
				+ " and q.objectInst=:objectInst"
				+ " and q.swsId=:swsId"
				+ " and q.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm=null;
		try{
			perm=(SwsPermission)q.getSingleResult();
		}catch(NoResultException e){
			return new ArrayList<>();
		}
		
		return getAuthAsk(perm.getId(),p.getSwsId());
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public SwsAuthAsk createQuestion(CreateQuestionParameters p)
			throws CircuitException {
		String jpql = "select q from SwsPermission q where q.objectName=:objectName"
				+ " and q.objectInst=:objectInst"
				+ " and q.swsId=:swsId"
				+ " and q.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm=null;
		try{
			perm=(SwsPermission)q.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
		SwsAuthAsk a=new SwsAuthAsk();
		a.setPermissionId(perm.getId());
		a.setAnswer(p.getQ());
		a.setAsk(p.getA());
		a.setSwsId(p.getSwsId());
		em.persist(a);
		return a;
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public void delQuestion(DelQuestionParameters p)
			throws CircuitException {
		String jpql="delete from SwsAuthAsk a where a.id=:aqid or (a.ask=:a and a.answer=:q)";
		Query q=em.createQuery(jpql);
		q.setParameter("aqid", p.getAqid());
		q.setParameter("a", p.getA());
		q.setParameter("q", p.getQ());
		q.executeUpdate();
	}
	@CjTransaction(unitName = "sosdb")
	@Override
	public void updateQuestion(UpdateQuestionParameters p)
			throws CircuitException {
		String jpql = "select q from SwsPermission q where q.objectName=:objectName"
				+ " and q.objectInst=:objectInst"
				+ " and q.swsId=:swsId"
				+ " and q.permissionCode=:permissionCode";
		Query q = em.createQuery(jpql);
		q.setParameter("objectInst", p.getObjectInst());
		q.setParameter("objectName", p.getObjectName());
		q.setParameter("permissionCode", p.getPermissionCode());
		q.setParameter("swsId", p.getSwsId());
		SwsPermission perm=null;
		try{
			perm=(SwsPermission)q.getSingleResult();
		}catch(NoResultException e){
			return ;
		}
		jpql="select a from SwsAuthAsk a where a.id=:aqid or (a.ask=:a and a.answer=:q)";
		q=em.createQuery(jpql);
		q.setParameter("aqid", p.getAqid());
		q.setParameter("a", p.getA());
		q.setParameter("q", p.getQ());
		SwsAuthAsk a=null;
		try{
		a=(SwsAuthAsk)q.getSingleResult();
		}catch(NoResultException e){
			a=new SwsAuthAsk();
			a.setPermissionId(perm.getId());
			a.setAnswer(p.getQ());
			a.setAsk(p.getA());
			a.setSwsId(p.getSwsId());
			em.persist(a);
			return;
		}
		a.setPermissionId(perm.getId());
		a.setAnswer(p.getQ());
		a.setAsk(p.getA());
		a.setSwsId(p.getSwsId());
		em.merge(a);
//		em.refresh(a);
	}
}
