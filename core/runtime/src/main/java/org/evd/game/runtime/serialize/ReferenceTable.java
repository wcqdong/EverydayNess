package org.evd.game.runtime.serialize;

/**
 *
 *
 * {@link OutputStream} 和 {@link InputStream} 使用的字符串查找表，用来优化字符串序列化
 */
public abstract class ReferenceTable {
	/** 单例 */
	public static ReferenceTable INSTANCE = null;
	
	/**
	 * 获取对象的id
	 * @param obj
	 * @return
	 */
	public abstract int getId(Object obj);
	
	/**
	 * 通过id获取对象
	 * @param id
	 * @return
	 */
	public abstract Object getObject(int id);
}
