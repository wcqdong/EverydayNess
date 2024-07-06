package org.evd.game.runtime.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * node启动之前会先执行各工程中的Starter函数
 * 标注Starter类和Starter函数
 * 函数必须是静态的 && 参数为Node
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Module {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public static @interface OnStart {
        int priority() default 100;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public static @interface OnEnd{
        int priority() default 100;
    }
}


