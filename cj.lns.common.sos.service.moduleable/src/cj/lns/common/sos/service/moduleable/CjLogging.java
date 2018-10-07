package cj.lns.common.sos.service.moduleable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解日志
 * 
 * <pre>
 * </pre>
 * 
 * @author carocean
 * @see IEntityManagerable
 */
@Target(value = { ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface CjLogging {

}
