package org.evd.game.runtime.support.function;

/**
 * 接受0个参数的返回函数
 * @param <R>
 */
@FunctionalInterface
public interface ReturnFunction0<R> {
	
	R apply();

}
