package cj.lns.common.sos.service.moduleable;

import javax.persistence.EntityManager;

/**
 * 实体管理器，开发者使用transaction方面时必须实现此接口
 * <pre>
 * －作用是传入实现管理器
 * 
 * －说明jpa的工厂是采用持久上文且是注解工厂机制实现跨段事务的（目前仅对之研究到此），
 *   而通过编程方式却不知道在哪将工厂给事务化，因此，本事务机制仍是折哀手段，它：
 *   只是能过方面拦截服务的调用，在方法执行前注入实体管理器，并开启实体管理器的事务，
 *   方法执行后由方面提交事务，因此：
 *   
 *   应用程序中不必显示再调用事务，以免造成不必要的事务苷套。
 * </pre>
 * @author carocean
 *
 * @see CjTransaction CjTransaction事务注解
 */
public interface IEntityManagerable {
void setEntityManager(EntityManager em);
}
