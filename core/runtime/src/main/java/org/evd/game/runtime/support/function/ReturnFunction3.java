package org.evd.game.runtime.support.function;

/**
 * 接受3个参数的返回函数
 * @param <R>
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
@FunctionalInterface
public interface ReturnFunction3<R, T1, T2, T3> {
	
	R apply(T1 t1, T2 t2, T3 t3);

}
