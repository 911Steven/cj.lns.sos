package cj.lns.chip.sos.service.builder.db.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import cj.lns.common.sos.service.model.SosProperty;
import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;

@CjService(name = "dbBuilderService")
public class DbBuilderService implements IDbBuilderService {
	@Override
	public void init(String sosid) {
		EntityManagerFactory f = ServiceosServiceModule.get().site().factory("sosdb");
		EntityManager em = f.createEntityManager();
		EntityTransaction tran = em.getTransaction();
		try {
			tran.begin();
			SosProperty sp = new SosProperty();
			sp.setPropName("sos.id");
			sp.setPropValue(sosid);
			em.persist(sp);
			tran.commit();
		} catch (Exception e) {
			if(tran.isActive())
				tran.rollback();
			throw new EcmException(String.format("数据库初始化失败. 原因：%s", e));
		} finally {
			em.close();
		}
	}
}
