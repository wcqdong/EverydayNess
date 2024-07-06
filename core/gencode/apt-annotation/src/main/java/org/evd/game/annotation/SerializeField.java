package org.evd.game.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 序列化字段注解
 * @author zenghongming
 * @date 2020/02/09 16:59
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface SerializeField {
    /**
     * 是否不检查字段类型
     * @return 不检查 true
     */
    boolean uncheck() default false;
}
