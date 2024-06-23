package org.evd.runtime.support.function;

/**
 * 接受4个参数的函数
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 */
@FunctionalInterface
public interface Function4<T1, T2, T3, T4> {
	
	void apply(T1 t1, T2 t2, T3 t3, T4 t4);

}
