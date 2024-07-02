package org.evd.game.runtime.serialize;

/**
 *
 *
 * 序列化的各种数据格式
 */
public class WireFormat {
	/** 数组 */
	public static final int ARRAY			= 0b0001;
	
	/** 空对象 */
	public static final int NULL			= 0;
	
	/** 基本类型 */
	public static final int BYTE			= 2;
	public static final int BOOLEAN			= 4;
	public static final int INT				= 6;
	public static final int LONG			= 8;
	public static final int FLOAT			= 10;
	public static final int DOUBLE			= 12;
	public static final int STRING			= 14;
	public static final int ENUM			= 16;
	public static final int OBJECT			= 20;
	
	/** 容器类型 */
	/** 内置支持List对象 */
	public static final int LIST			= 22;
	/** 内置支持Set对象 */
	public static final int SET				= 24;
	/** 内置支持Map对象 */
	public static final int HashMAP = 26;
	/** 内置支持COLLECTION对象 */
	public static final int COLLECTION		= 28;
	/** 内置支持Queue对象*/
	public static final int QUEUE			= 30;
	/** 引用类型 */
	public static final int REFERENCE		= 32;
	/** linkedList */
	public static final int LINKED_lIST		= 34;
	/** linkedHashMap */
	public static final int LINKED_MAP		= 36;
	
	/** 特殊类型 */
	/** 支持继承了IDistributedSerilizable接口的对象 */
	public static final int DISTRIBUTED		= 50;
	/** ProtoBuff消息类型 */
	public static final int MSG				= 52;
}
