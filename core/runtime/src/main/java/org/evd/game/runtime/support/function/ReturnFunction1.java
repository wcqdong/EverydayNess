package org.evd.game.runtime.support.function;

/**
 * 接受1个参数的返回函数
 * @param <R>
 * @param <T1>
 */
@FunctionalInterface
public interface ReturnFunction1<R, T1> {
	
	R apply(T1 t1);

}
