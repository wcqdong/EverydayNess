package org.evd.runtime.support;

/**
 * 
 *
 * 元组相关
 */
public class TupleUtils {
	/**
	 * 创建一个2元组
	 * @param first
	 * @param second
	 * @return
	 */
	public static <A, B> TwoTuple<A, B> tuple(A first, B second) {
		return new TwoTuple<A, B>(first, second);
	}
	
	/**
	 * 创建一个3元组
	 * @param first
	 * @param second
	 * @param third
	 * @return
	 */
	public static <A, B, C> ThreeTuple<A, B, C> tuple(A first, B second, C third) {
		return new ThreeTuple<A, B, C>(first, second, third);
	}
	
	/**
	 * 创建一个4元组
	 * @param first
	 * @param second
	 * @param third
	 * @param fourth
	 * @return
	 */
	public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A first, B second, C third, D fourth) {
		return new FourTuple<A, B, C, D>(first, second, third, fourth);
	}
}
