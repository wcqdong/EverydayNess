package org.evd.runtime.support.function;

/**
 * 接受1个参数的函数
 * @param <T1>
 */
@FunctionalInterface
public interface Function1<T1> {
	
	void apply(T1 t1) throws InterruptedException;

}
