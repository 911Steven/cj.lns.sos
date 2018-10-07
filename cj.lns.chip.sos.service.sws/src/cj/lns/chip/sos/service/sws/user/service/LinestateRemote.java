package cj.lns.chip.sos.service.sws.user.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import cj.lns.chip.sos.service.framework.IRemoteService;
import cj.lns.chip.sos.service.framework.RemoteResult;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteMethod;
import cj.lns.chip.sos.service.framework.annotation.CjRemoteService;
import cj.lns.common.sos.service.model.SosLinestate;
import cj.lns.common.sos.service.moduleable.CjTransaction;
import cj.lns.common.sos.service.moduleable.IEntityManagerable;
import cj.studio.ecm.annotation.CjBridge;
import cj.studio.ecm.annotation.CjExotericalType;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.graph.CircuitException;

@CjExotericalType(typeName = "remoteService")
@CjRemoteService(usage = "联系人状态服务")
@CjBridge(aspects = "logging+transaction")
@CjService(name = "/third/sns/linestate/", isExoteric = true)
public class LinestateRemote implements IRemoteService, IEntityManagerable {
	private EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@CjTransaction(unitName = "sosdb")
	@CjRemoteMethod(usage = "更新用户在线状态", returnContentType = "text/json", returnUsage = "无")
	public RemoteResult update(LinestateParameters p) throws CircuitException {
		RemoteResult r = new RemoteResult(200, "ok");
		SosLinestate line = null;
		switch (p.command) {
		case "online":
			line = getLineState(p.peerId);
			boolean exists = true;
			if (line == null) {
				exists = false;
				line = new SosLinestate();
			}
			line.setOnDevice(p.onDevice);
			line.setOnServicews(p.onServicews);
			line.setOnTerminus(p.onTerminus);
			line.setPeerId(p.peerId);
			line.setState(p.lineState);
			line.setUser(p.user);
			if (exists) {
				em.merge(line);
			} else {
				em.persist(line);
			}
			break;
		case "offline":
			line = getLineState(p.peerId);
			if(line!=null)
			em.remove(line);
			break;
		case "changestate":
			line = getLineState(p.peerId);
			if (line == null) {
				throw new CircuitException("404",
						"改变在线状态失败，peer不存在：" + p.peerId);
			}
			line.setOnDevice(p.onDevice);
			line.setOnServicews(p.onServicews);
			line.setOnTerminus(p.onTerminus);
			line.setPeerId(p.peerId);
			line.setState(p.lineState);
			line.setUser(p.user);
			em.merge(line);
			break;
		}
		return r;
	}

	private SosLinestate getLineState(String peerId) {
		String ql="select ls from SosLinestate ls where ls.peerId=:peerId";
		Query q=em.createQuery(ql);
		q.setParameter("peerId", peerId);
		try{
			return (SosLinestate)q.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

}
