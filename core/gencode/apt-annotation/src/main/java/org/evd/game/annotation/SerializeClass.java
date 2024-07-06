package org.evd.game.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可序列化注解
 * @author zenghongming
 * @date 2020/02/09 16:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SerializeClass {
    /** 是否使用自定义序列化 */
    boolean customized() default false;
}
