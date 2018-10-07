package cj.lns.chip.sos.service.sws.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;
import cj.ultimate.gson2.com.google.gson.Gson;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "联系人关系查询服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/third/sns/relationship", isExoteric = true)
public class RelationshipRemote implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		// TODO Auto-generated method stub
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "查询加指定用户为好友的所有用户", returnContentType = "text/json", returnUsage = "内容：json array。一般由社区系统调用")
	public RemoteResult get(GetUsersByRelationshipParameters p)
			throws CircuitException {
		String jpql = "select si.owner from SwsInfo si ,SwsContact sc  where si.id=sc.swsId and sc.userCode=:userCode";
		Query q = em.createQuery(jpql);
		q.setParameter("userCode", p.getUserCode());
		List<?> list=q.getResultList();
		List<String> users=new ArrayList<>();
		for(Object o:list){
			users.add((String)o);
		}
		RemoteResult r = new RemoteResult(200, "ok");
		r.content().writeBytes(new Gson().toJson(users).getBytes());
		return r;
	}

}
