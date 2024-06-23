package org.evd.runtime.support.function;


/**
 * 接受2个参数的函数
 * @param <T1>
 * @param <T2>
 */
@FunctionalInterface
public interface Function2<T1, T2> {
	
	void apply(T1 t1, T2 t2) throws InterruptedException;

}
