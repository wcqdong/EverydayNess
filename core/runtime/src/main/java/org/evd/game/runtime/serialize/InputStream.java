package org.evd.game.runtime.serialize;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.GeneratedMessage;
import org.evd.game.base.ISerializable;
import org.evd.game.base.InputStreamBase;
import org.evd.game.runtime.Chunk;
import org.evd.game.runtime.support.SysException;
import org.evd.game.runtime.support.function.ReturnFunction2;
import org.evd.game.runtime.support.function.ReturnFunctionWithException1;
import org.evd.game.runtime.support.function.ReturnFunctionWithException2;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static org.evd.game.runtime.serialize.WireFormat.*;

/**
 *
 *
 * 输入处理类，用来反序列化
 */
public class InputStream implements InputStreamBase {
	
	/** 字节流处理类 */
	private final CodedInputStream stream;

	private static final Map<Integer, ReturnFunctionWithException1<ISerializable, InputStream, IOException>> _serializeReadFuncMap = new HashMap<>();
//	private static ReturnFunctionWithException2<ISerializable, Integer, InputStream, IOException> _serializeReadFunc = null;

	/** 反序列化Msg的函数 */
	private static ReturnFunction2<GeneratedMessage, Integer, CodedInputStream> _msgFunc = null;
	/** 反序列化Enum函数 */
//	private static ReturnFunction2<Enum, Integer, String> _enumFunc = null;
	private static final Map<Integer, ReturnFunctionWithException2<Enum<?>, InputStream, Integer, IOException>> _enumFuncMap = new HashMap<>();
	
	/**
	 * 设置反序列化Msg的函数
	 * @param msgFunc 反序列化Msg的函数
	 */
	public static void setCreateMsgFunc(ReturnFunction2<GeneratedMessage, Integer, CodedInputStream> msgFunc) {
		_msgFunc = msgFunc;
	}
	
//	/**
//	 * 设置反序列化枚举函数
//	 * @param enumFunc 反序列化枚举函数
//	 */
//	public static void setCreateEnumFunc(ReturnFunction2<Enum, Integer, String> enumFunc) {
//		_enumFunc = enumFunc;
//	}
	
	public InputStream(Chunk chunk) {
		this(chunk.buffer, chunk.offset, chunk.length);
	}
	
	public InputStream(byte[] buffer, int offset, int length) {
		this.stream = CodedInputStream.newInstance(buffer, offset, length);
	}
	
	/**
	 * 是否已全部读取完毕
	 * @return
	 */
    public boolean isAtEnd() {
		try {
			return stream.isAtEnd();
		} catch (IOException e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 从流中读取数据
	 * 只能顺序读 会自动进行类型转换
	 * @return
	 */
	@Override
	public <T> T read() {
		try {
			return readObject();
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 从流中读取数据
	 * 只能顺序读 会自动进行类型转换
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	private <T> T readObject() throws Exception {
		Object result = null;

		// 类型码
		int wireFormat = stream.readInt32();
		// 类型
		int wireType = (wireFormat & ~ARRAY);
		// 是数组类型
		boolean isArray = (wireFormat & ARRAY) == ARRAY;

		// 空对象
		if (wireType == NULL) {
			return null;
		}
		
		// REFERENCE
		if (wireType == REFERENCE) {
			result = doReadReference();
		// BYTE
		} else if (wireType == BYTE) {
			if (isArray) {
				result = doReadByteArray();
			} else {
				result = doReadByte();
			}
		// BOOLEAN
		} else if (wireType == BOOLEAN) {
			if (isArray) {
				result = doReadBooleanArray();
			} else {
				result = doReadBoolean();
			}
		// INT
		} else if (wireType == INT) {
			if (isArray) {
				result = doReadIntArray();
			} else {
				result = doReadInt();
			}
		// LONG
		} else if (wireType == LONG) {
			if (isArray) {
				result = doReadLongArray();
			} else {
				result = doReadLong();
			}
		// FLOAT
		} else if (wireType == FLOAT) {
			if (isArray) {
				result = doReadFloatArray();
			} else {
				result = doReadFloat();
			}
		// DOUBLE
		} else if (wireType == DOUBLE) {
			if (isArray) {
				result = doReadDoubleArray();
			} else {
				result = doReadDouble();
			}
		// STRING
		} else if (wireType == STRING) {
			if (isArray) {
				result = doReadStringArray();
			} else {
				result = doReadString();
			}
		// ENUM
		} else if (wireType == ENUM) {
			result = doReadEnum();
		// COLLECTION LIST SET
		} else if (wireType == COLLECTION || wireType == LIST || wireType == SET ||
				wireType == QUEUE || wireType == LINKED_lIST) {
			// 类型
			if (wireType == LIST) {
				result = doReadArrayList();
			} else if (wireType == SET) {
				result = doReadSet();
			} else if (wireType == QUEUE) {
				result = doReadQueue();
			} else if (wireType == LINKED_lIST){
				result = doReadLinkedList();
			} else {
				// 未知Collection的具体实现 暂时一律使用arrayList子类的实现
				result = doReadArrayList();
			}
		// MAP
		} else if (wireType == HashMAP || wireType == LINKED_MAP) {
			if (wireType == LINKED_MAP) {
				result = doReadLinkedHashMap();
			} else {
				result = doReadMap();
			}
		// IDistributedSerilizable接口
		} else if (wireType == DISTRIBUTED) {
			result = doReadDistributed();
		// protobuf消息
		} else if (wireType == MSG) {
			result = doReadProtoMsg();
		// Object[]
		} else if (wireType == OBJECT && isArray) {
			result = doReadObjectArray();
		// 其余一律不支持
		} else {
			throw new SysException("发现无法被Distributed反序列化的类型: wireType={}, isArray={}", wireType, isArray);
		}
		
		// 返回值
		return (T) result;
	}

	//********************************************* read *********************************************//
	//********************************** 必须在逻辑上保证读写的顺序 **********************************//
	/**
	 * 读一个byte
	 * 与{@link OutputStream}的writeByte配对使用
	 * @return byte值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public byte readByte() throws IOException {
		if (!checkValid(BYTE)) {
			return 0;
		}
		return doReadByte();
	}

	private byte doReadByte() throws IOException {
		return stream.readRawByte();
	}

	/**
	 * 读一个byteArray
	 * 与{@link OutputStream}的writeByteArray配对使用
	 * @return byteArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public byte[] readByteArray() throws IOException {
		if (!checkValid(BYTE | ARRAY)) {
			return null;
		}
		return doReadByteArray();
	}

	private byte[] doReadByteArray() throws IOException {
		int len = stream.readInt32();
		return stream.readRawBytes(len);
	}

	/**
	 * 读一个布尔值
	 * 与{@link OutputStream}的writeBoolean配对使用
	 * @return boolean值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public boolean readBoolean() throws IOException {
		if (!checkValid(BOOLEAN)) {
			return false;
		}
		return doReadBoolean();
	}

	private boolean doReadBoolean() throws IOException {
		return stream.readBool();
	}

	/**
	 * 读一个booleanArray
	 * 与{@link OutputStream}的writeBooleanArray配对使用
	 * @return booleanArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public boolean[] readBooleanArray() throws IOException {
		if (!checkValid(BOOLEAN | ARRAY)) {
			return null;
		}
		return doReadBooleanArray();
	}

	private boolean[] doReadBooleanArray() throws IOException {
		int len = stream.readInt32();
		boolean[] array = new boolean[len];
		for (int i = 0; i< len; ++i) {
			array[i] = stream.readBool();
		}
		return array;
	}

	/**
	 * 读一个int
	 * 与{@link OutputStream}的writeInt配对使用
	 * @return int值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public int readInt() throws IOException {
		if (!checkValid(INT)) {
			return 0;
		}
		return doReadInt();
	}

	private int doReadInt() throws IOException {
		return stream.readInt32();
	}

	/**
	 * 读一个intArray
	 * 与{@link OutputStream}的writeIntArray配对使用
	 * @return intArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public int[] readIntArray() throws IOException {
		if (!checkValid(INT | ARRAY)) {
			return null;
		}
		return doReadIntArray();
	}

	private int[] doReadIntArray() throws IOException {
		int len = stream.readInt32();
		int[] array = new int[len];
		for (int i = 0; i < len; ++i) {
			array[i] = stream.readInt32();
		}
		return array;
	}

	/**
	 * 读一个long
	 * 与{@link OutputStream}的writeLong配对使用
	 * @return long值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public long readLong() throws IOException {
		if (!checkValid(LONG)) {
			return 0L;
		}
		return doReadLong();
	}

	private long doReadLong() throws IOException {
		return stream.readInt64();
	}

	/**
	 * 读一个longArray
	 * 与{@link OutputStream}的writeLong配对使用
	 * @return long值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public long[] readLongArray() throws IOException {
		if (!checkValid(LONG | ARRAY)) {
			return null;
		}
		return doReadLongArray();
	}

	private long[] doReadLongArray() throws IOException {
		int len = stream.readInt32();
		long[] array = new long[len];
		for (int i = 0; i < len; ++i) {
			array[i] = stream.readInt64();
		}
		return array;
	}

	/**
	 * 读一个float
	 * 与{@link OutputStream}的writeFloat配对使用
	 * @return float值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public float readFloat() throws IOException {
		if (!checkValid(FLOAT)) {
			return 0f;
		}
		return doReadFloat();
	}

	private float doReadFloat() throws IOException {
		return stream.readFloat();
	}

	/**
	 * 读一个floatArray
	 * 与{@link OutputStream}的writeFloatArray配对使用
	 * @return floatArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public float[] readFloatArray() throws IOException {
		if (!checkValid(FLOAT | ARRAY)) {
			return null;
		}
		return doReadFloatArray();
	}

	private float[] doReadFloatArray() throws IOException {
		int len = stream.readInt32();
		float[] array = new float[len];
		for (int i = 0; i < len; ++i) {
			array[i] = stream.readFloat();
		}
		return array;
	}

	/**
	 * 读一个double
	 * 与{@link OutputStream}的writeDouble配对使用
	 * @return double值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public double readDouble() throws IOException {
		if (!checkValid(DOUBLE)) {
			return 0D;
		}
		return doReadDouble();
	}

	private double doReadDouble() throws IOException {
		return stream.readDouble();
	}

	/**
	 * 读一个doubleArray
	 * 与{@link OutputStream}的writeDoubleArray配对使用
	 * @return doubleArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public double[] readDoubleArray() throws IOException {
		if (!checkValid(DOUBLE | ARRAY)) {
			return null;
		}
		return doReadDoubleArray();
	}

	private double[] doReadDoubleArray() throws IOException {
		int len = stream.readInt32();
		double[] array = new double[len];
		for (int i = 0; i < len; ++i) {
			array[i] = stream.readDouble();
		}
		return array;
	}

	/**
	 * 读一个String,如果在reference中则从reference中取，否则读string
	 * 与{@link OutputStream}的writeString配对使用
	 * @return string值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@Override
	public String readString() throws IOException {
		int wireType = stream.readInt32();
		if (wireType == NULL) {
			return null;
		}
		if (wireType == REFERENCE) {
			return doReadReference();
		}
		if (wireType == STRING) {
			return doReadString();
		}
		throw new SysException("不匹配的序列化 readType:{}, writeType:{}", STRING, wireType);
	}

	private String doReadString() throws IOException {
		return stream.readString();
	}

	private String doReadReference() throws IOException {
		int referId = stream.readInt32();
		Object result = ReferenceTable.INSTANCE.getObject(referId);
		if (result == null) {
			throw new SysException("未知的引用ID={}", referId);
		}
		return (String) result;
	}

	/**
	 * 读一个stringArray
	 * 与{@link OutputStream}的writeStringArray配对使用
	 * @return stringArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public String[] readStringArray() throws IOException {
		if (!checkValid(STRING | ARRAY)) {
			return null;
		}
		return doReadStringArray();
	}

	private String[] doReadStringArray() throws IOException {
		int len = stream.readInt32();
		String[] array = new String[len];
		for (int i = 0; i < len; ++i) {
			array[i] = stream.readString();
		}
		return array;
	}

	/**
	 * 读一个Enum
	 * 与{@link OutputStream}的writeEnum配对使用
	 * @return enum值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public Enum<?> readEnum() throws IOException {
		if (!checkValid(ENUM)) {
			return null;
		}
		return doReadEnum();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Enum<?> doReadEnum() throws IOException {
		// 实际类型
//		String className = stream.readString();
//		String val = stream.readString();
//		Enum em = null;
//		// 先通过反序列化函数反序列化
//		if (_enumFunc != null) {
//			em = _enumFunc.apply(className.hashCode(), val);
//		}
//		if (em != null) {
//			return em;
//		}
//		// 通过函数不能反序列化，则再次通过反射创建实例
//		Class cls = Class.forName(className);
//		return Enum.valueOf(cls, val);
		int id = stream.readInt32();
		int ordinal = stream.readInt32();
		ReturnFunctionWithException2<Enum<?>, InputStream, Integer, IOException> func = _enumFuncMap.get(id);
		if (func == null) {
			throw new SysException("未能找到序列化对象, hashCode: {}", id);
		}
		return func.apply(this, ordinal);
	}

	/**
	 * 读一个List(ArrayList or LinkedList)
	 * 与{@link OutputStream}的writeList配对使用
	 * @return List值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <E> List<E> readList() throws IOException {
		int wireFormat = stream.readInt32();
		if (wireFormat == NULL) {
			return null;
		}
		if (wireFormat == LINKED_lIST) {
			return doReadLinkedList();
		}
		return doReadArrayList();
	}

	/**
	 * 读一个ArrayList
	 * 与{@link OutputStream}的writeList配对使用
	 * @return List值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <E> ArrayList<E> readArrayList() throws IOException {
		if (!checkValid(LIST)) {
			return null;
		}
		return doReadArrayList();
	}

	private <E> ArrayList<E> doReadArrayList() throws IOException {
		int len = stream.readInt32();
		ArrayList<E> list = new ArrayList<>();
		for (int i = 0; i < len; ++i) {
			list.add(this.read());
		}
		return list;
	}

	/**
	 * 读一个linkedList
	 * 与{@link OutputStream}的writeLinkedList配对使用
	 * @return List值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <E> LinkedList<E> readLinkedList() throws IOException {
		if (!checkValid(LINKED_lIST)) {
			return null;
		}
		return doReadLinkedList();
	}

	private <E> LinkedList<E> doReadLinkedList() throws IOException {
		int len = stream.readInt32();
		LinkedList<E> list = new LinkedList<>();
		for (int i = 0; i < len; ++i) {
			list.add(this.read());
		}
		return list;
	}

	/**
	 * 读一个Set
	 * 与{@link OutputStream}的writeSet配对使用
	 * @return Set值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <E> Set<E> readSet() throws IOException {
		if (!checkValid(SET)) {
			return null;
		}
		return doReadSet();
	}

	public <E> Set<E> doReadSet() throws IOException {
		int len = stream.readInt32();
		Set<E> set = new HashSet<>();
		for (int i = 0; i < len; ++i) {
			set.add(this.read());
		}
		return set;
	}

	/**
	 * 读一个Queue
	 * 与{@link OutputStream}的writeQueue配对使用
	 * @return Deque值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <E> Queue<E> readQueue() throws IOException {
		if (!checkValid(QUEUE)) {
			return null;
		}
		return doReadQueue();
	}

	private <E> Queue<E> doReadQueue() throws IOException {
		int len = stream.readInt32();
		Queue<E> queue = new ArrayDeque<>();
		for (int i = 0; i < len; ++i) {
			queue.add(this.read());
		}
		return queue;
	}

	/**
	 * 读一个Map
	 * 与{@link OutputStream}的writeMap配对使用
	 * @return Map值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <K, V> Map<K, V> readMap() throws IOException {
		if (!checkValid(HashMAP)) {
			return null;
		}
		return doReadMap();
	}

	private <K, V> Map<K, V> doReadMap() throws IOException {
		int len = stream.readInt32();
		Map<K, V> map = new HashMap<>(len);
		for (int i = 0; i < len; i++) {
			map.put(this.read(), this.read());
		}
		return map;
	}

	/**
	 * 读一个linkedHashMap
	 * 与{@link OutputStream}的writeLinkedHashMap配对使用
	 * @return Map值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <K, V> Map<K, V> readLinkedHashMap() throws IOException {
		if (!checkValid(LINKED_MAP)) {
			return null;
		}
		return doReadLinkedHashMap();
	}

	private <K, V> Map<K, V> doReadLinkedHashMap() throws IOException {
		int len = stream.readInt32();
		Map<K, V> map = new LinkedHashMap<>(len);
		for (int i = 0; i < len; i++) {
			map.put(this.read(), this.read());
		}
		return map;
	}

	/**
	 * 读一个ISerilizable
	 * 与{@link OutputStream}的writeDistributed配对使用
	 * @return ISerilizable值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T readDistributed() throws IOException {
		if (!checkValid(DISTRIBUTED)) {
			return null;
		}
		return (T) doReadDistributed();
	}

	private ISerializable doReadDistributed() throws IOException {
		int id = stream.readInt32();
		ReturnFunctionWithException1<ISerializable, InputStream, IOException> func = _serializeReadFuncMap.get(id);
		if (func == null) {
			throw new SysException("未能找到序列化对象, hashCode: {}", id);
		}
		return func.apply(this);
	}

	/**
	 * 读一个protoMsg
	 * 与{@link OutputStream}的writeProtoMsg配对使用
	 * @return GeneratedMessage值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public GeneratedMessage readProtoMsg() throws IOException {
		if (!checkValid(MSG)) {
			return null;
		}
		return doReadProtoMsg();
	}

	private GeneratedMessage doReadProtoMsg() throws IOException {
		int len = stream.readInt32();
		int id = stream.readInt32();
		byte[] bytes = stream.readRawBytes(len);
		// 取出消息体
		return _msgFunc.apply(id, CodedInputStream.newInstance(bytes));
	}

	/**
	 * 读一个ObjectArray
	 * 与{@link OutputStream}的writeObjectArray配对使用
	 * @return ObjectArray值
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public <T> T[] doReadObjectArray(Function<Integer, T[]> func) throws IOException {
		if (!checkValid(OBJECT | ARRAY)) {
			return null;
		}
		int len = stream.readInt32();
		T[] array = func.apply(len);
		for (int i = 0; i < len; ++i) {
			array[i] = this.read();
		}
		return array;
	}

	public Object[] readObjectArray() throws IOException {
		if (!checkValid(OBJECT | ARRAY)) {
			return null;
		}
		int len = stream.readInt32();
		Object[] array = new Object[len];
		for (int i = 0; i < len; ++i) {
			array[i] = this.read();
		}
		return array;
	}

	private Object[] doReadObjectArray() throws IOException {
		int len = stream.readInt32();
		Object[] array = new Object[len];
		for (int i = 0; i < len; ++i) {
			array[i] = this.read();
		}
		return array;
	}

	/**
	 * 读取一个值判断是否为null
	 * @return 是否是null
	 * @throws IOException InvalidProtocolBufferException IllegalStateException
	 */
	public boolean readNull() throws IOException {
		return stream.readInt32() == NULL;
	}

	/**
	 * 检查值是否正确
	 * @param readType 类型
	 * @return 是否正确
	 */
	private boolean checkValid(int readType) throws IOException {
		// 类型码
		int wireFormat = stream.readInt32();
		if (wireFormat == NULL) {
			return false;
		}

		if (wireFormat != readType) {
			throw new SysException("不匹配的序列化 writeType:{}, readType:{}", wireFormat, readType);
		}

		return true;
	}

	@Override
	public byte[] readBytes() throws IOException {
		return new byte[0];
	}

	@Override
	public short readShort() throws IOException {
		throw new SysException("未实现readShort");
	}

	public static void registerSerializeReadFunc(int key, ReturnFunctionWithException1<ISerializable, InputStream, IOException> func){
		ReturnFunctionWithException1<ISerializable, InputStream, IOException> oldFunc = _serializeReadFuncMap.put(key, func);
		if (oldFunc != null){
			throw new SysException("register repeated serialize func");
		}
	}

	public static void registerSerializeReadEnumFunc(int key, ReturnFunctionWithException2<Enum<?>, InputStream, Integer, IOException> func){
		ReturnFunctionWithException2<Enum<?>, InputStream, Integer, IOException> oldFunc = _enumFuncMap.put(key, func);
		if (oldFunc != null){
			throw new SysException("register repeated enum serialize func");
		}
	}
}
