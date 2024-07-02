package org.evd.game.runtime.support.function;

/**
 * 2个参数
 * @param <T1>
 * @param <T2>
 */
@FunctionalInterface
public interface Function2WithException<T1, T2, E extends Exception> {
	
	void apply(T1 t1, T2 t2) throws E;

}
