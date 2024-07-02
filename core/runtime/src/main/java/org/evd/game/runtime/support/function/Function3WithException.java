package org.evd.game.runtime.support.function;

/**
 * 3个参数
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
@FunctionalInterface
public interface Function3WithException<T1, T2, T3, E extends Exception> {
	
	void apply(T1 t1, T2 t2, T3 t3) throws E;

}
