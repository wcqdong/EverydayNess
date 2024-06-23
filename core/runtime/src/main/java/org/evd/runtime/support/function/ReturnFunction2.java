package org.evd.runtime.support.function;

/**
 * 接受2个参数的返回函数
 * @param <R>
 * @param <T1>
 * @param <T2>
 */
@FunctionalInterface
public interface ReturnFunction2<R, T1, T2> {
	
	R apply(T1 t1, T2 t2);

}
