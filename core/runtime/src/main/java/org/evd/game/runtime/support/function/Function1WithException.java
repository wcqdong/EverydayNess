package org.evd.game.runtime.support.function;

/**
 * 1个参数
 * @param <T1>
 */
@FunctionalInterface
public interface Function1WithException<T1, E extends Exception> {
	
	void apply(T1 t1) throws E;

}
