package cj.lns.chip.sos.service.framework.service;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjService;

@CjBridge(aspects = "logging+transaction")
@CjService(name = "idGenerator")
public class IDGenerator implements IEntityManagerable, IIDGenerator {

	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IIDGenerator#genUserId(int, int)
	 */
	@Override
	@CjTransaction(unitName="sosdb")
	public BigInteger genUserId(int kindCode,int userSetCode){
		String jpql="select count(*) from SosIdGenerator id where id.swsId is not null";
		Query q=em.createQuery(jpql);
		Object ret=q.getSingleResult();
		int userCount=(Integer)ret;
		jpql="update SosIdGenerator id set id.userId=:userId";
		q=em.createQuery(jpql);
		q.setParameter("userId", userCount);
		q.executeUpdate();
		BigInteger bi=new BigInteger(String.format("%s%s%s", kindCode,userSetCode,userCount));
		return bi;
	}
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.service.IIDGenerator#genSwsId(int, int)
	 */
	@Override
	@CjTransaction(unitName="sosdb")
	public BigInteger genSwsId(int kindCode,int userSetCode){
		String jpql="select count(id) from SosIdGenerator id where id.swsId is not null";
		Query q=em.createQuery(jpql);
		Object ret=q.getSingleResult();
		int swsCount=(Integer)ret;
		jpql="update SosIdGenerator id set id.swsId=:swsId";
		q=em.createQuery(jpql);
		q.setParameter("swsId", swsCount);
		q.executeUpdate();
		BigInteger bi=new BigInteger(String.format("%s%s%s", kindCode,userSetCode,swsCount));
		return bi;
	}
}
