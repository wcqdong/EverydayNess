package org.evd.runtime.support.function;

/**
 * 接受10个参数的返回函数
 * @param <R>
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @param <T5>
 * @param <T6>
 * @param <T7>
 * @param <T8>
 * @param <T9>
 * @param <T10>
 */
@FunctionalInterface
public interface ReturnFunction10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
	
	R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

}
