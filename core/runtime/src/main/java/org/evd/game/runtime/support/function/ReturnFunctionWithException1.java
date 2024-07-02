package org.evd.game.runtime.support.function;

/**
 *
 *
 * 2个参数1个返回值的函数(带异常的版本)
 * @param <R>
 * @param <T1>
 * @param <E>
 */
@FunctionalInterface
public interface ReturnFunctionWithException1<R, T1, E extends Exception> {
	
	R apply(T1 t1) throws E;

}
