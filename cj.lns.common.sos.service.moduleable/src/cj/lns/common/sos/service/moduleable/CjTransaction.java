package cj.lns.common.sos.service.moduleable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解事务
 * 
 * <pre>
 * 使用该注解需要实现IEntityManagerable接口
 * </pre>
 * 
 * @author carocean
 * @see IEntityManagerable
 */
@Target(value = { ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface CjTransaction {
	/**
	 * 指定jpa的持久化单元，即数据库名
	 * 
	 * <pre>
	 *
	 * </pre>
	 * 
	 * @return
	 */
	String unitName();
}
