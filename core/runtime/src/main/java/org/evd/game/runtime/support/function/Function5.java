package org.evd.game.runtime.support.function;

/**
 * 接受5个参数的函数
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @param <T5>
 */
@FunctionalInterface
public interface Function5<T1, T2, T3, T4, T5> {
	
	void apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

}
