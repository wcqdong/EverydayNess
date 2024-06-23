package org.evd.runtime.support.function;

/**
 * 接受6个参数的函数
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @param <T5>
 * @param <T6>
 */
@FunctionalInterface
public interface Function6<T1, T2, T3, T4, T5, T6> {
	
	void apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

}
