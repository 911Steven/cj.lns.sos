package cj.lns.chip.sos.service.framework.remote;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.chip.sos.service.framework.remote.parameter.GenNumParameters;
import cj.lns.chip.sos.service.framework.remote.parameter.GenServicewsNumParameters;
import cj.lns.common.sos.service.model.SosIdGenerator;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "标识生成服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/framework/idgen/", isExoteric = true)
public class IdGeneratorRemote implements IRemoteService, IEntityManagerable, IIdGeneratorRemote {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.remote.IIdGeneratorRemote#genNum(cj.lns.chip.sos.service.framework.remote.parameter.GenNumParameters)
	 */
	@Override
	@CjTransaction(unitName="sosdb")
	@CjRemoteMethod(usage = "生成指定主题的编号", returnContentType = "text/json", returnUsage = "返回：head key is num")
	public RemoteResult genNum(GenNumParameters parameters)
			throws CircuitException {
		String jpql = "select id from SosIdGenerator id where id.subject=:subject";
		Query q = em.createQuery(jpql);
		q.setParameter("subject", parameters.getSubject());
		SosIdGenerator id = null;
		try {
			id = (SosIdGenerator) q.getSingleResult();
			BigInteger num=id.getCurrentNum().add(BigInteger.valueOf(1));
			id.setCurrentNum(num);
			em.merge(id);
		} catch (NoResultException e) {
			id = new SosIdGenerator();
			id.setCurrentNum(BigInteger.valueOf(1));
			id.setSubject(parameters.getSubject());
			em.persist(id);
		}
		
		RemoteResult result = new RemoteResult(200, "成功");
		result.head("num",id.getCurrentNum().toString());
		return result;
	}
	/* (non-Javadoc)
	 * @see cj.lns.chip.sos.service.framework.remote.IIdGeneratorRemote#genServicewsNum(cj.lns.chip.sos.service.framework.remote.parameter.GenServicewsNumParameters)
	 */
	@Override
	@CjTransaction(unitName="sosdb")
	@CjRemoteMethod(usage = "生成视窗编号", returnContentType = "text/json", returnUsage = "成功与否")
	public RemoteResult genServicewsNum(GenServicewsNumParameters parameters)
			throws CircuitException {
		String jpql = "select id from SosIdGenerator id where id.subject='servicews'";
		Query q = em.createQuery(jpql);
		SosIdGenerator id = null;
		try {
			id = (SosIdGenerator) q.getSingleResult();
			BigInteger num=id.getCurrentNum().add(BigInteger.valueOf(1));
			id.setCurrentNum(num);
			em.merge(id);
		} catch (NoResultException e) {
			id = new SosIdGenerator();
			id.setCurrentNum(BigInteger.valueOf(1));
			id.setSubject("servicews");
			em.persist(id);
		}
		
		RemoteResult result = new RemoteResult(200, "成功");
		String v=parameters.getAssignedNum()+""+id.getCurrentNum();
		BigInteger whole=new BigInteger(v);
		result.head("wholeNum",whole.toString());
		result.head("reedomNum",id.getCurrentNum().toString());
		return result;
	}

}
