package cj.lns.chip.sos.service.sws;

import javax.persistence.EntityManagerFactory;

import cj.lns.common.sos.service.moduleable.ServiceosServiceModule;
/**
 * 服务的基类，提供实体工厂获取方法
 * <pre>
 *
 * </pre>
 * @author carocean
 *
 */
public class BaseService {
	protected EntityManagerFactory factory(){
		return ServiceosServiceModule.get().site().factory("sosdb");
	}
}
