package org.evd.game.runtime.support.function;

/**
 *
 *
 * 2个参数1个返回值的函数(带异常的版本)
 * @param <R>
 * @param <T1>
 * @param <T2>
 * @param <E>
 */
@FunctionalInterface
public interface ReturnFunctionWithException2<R, T1, T2, E extends Exception> {
	
	R apply(T1 t1, T2 t2) throws E;

}
