package org.evd.game.runtime.support.function;

/**
 * 接受3个参数的函数
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
@FunctionalInterface
public interface Function3<T1, T2, T3> {
	
	void apply(T1 t1, T2 t2, T3 t3);

}
