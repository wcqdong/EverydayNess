package org.evd.game.runtime.serialize;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.CodedOutputStream.OutOfSpaceException;
import com.google.protobuf.Message;
import org.evd.game.base.ISerializable;
import org.evd.game.base.OutputStreamBase;
import org.evd.game.runtime.*;
import org.evd.game.runtime.call.CallBase;
import org.evd.game.runtime.support.LogCore;
import org.evd.game.runtime.support.SysException;
import org.evd.game.runtime.support.function.Function2WithException;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static org.evd.game.runtime.serialize.WireFormat.*;

public class OutputStream implements AutoCloseable, OutputStreamBase {
	/** 字节流处理类 */
	private CodedOutputStream stream;
	
	/** 字节流最大长度 */
	private int lengthTotal;
	/** 字节流数据 */
	private byte[] buffer;

	private static final Map<Integer, Function2WithException<OutputStream, ISerializable, IOException>> serializeWriteFuncMap = new HashMap<>();
//	private static Function3WithException<Integer, OutputStream, ISerializable, IOException> _serializeWriteFunc = null;
	
	public OutputStream() {
		this(BufferPool.allocate());
	}
	
	public OutputStream(byte[] buffer) {
		this.lengthTotal = buffer.length;
		this.buffer = buffer;
		
		this.stream = CodedOutputStream.newInstance(buffer);
	}
	
	/**
	 * 重置
	 */
	void reset() {
		stream = CodedOutputStream.newInstance(buffer);
	}
	
	/**
	 * 关闭
	 */
	@Override
	public void close() {
		BufferPool.deallocate(buffer);
		buffer = null;
		stream = null;
	}
	
	/**
	 * 获取流的已使用长度
	 * @return
	 */
	public int getLength() {
		return lengthTotal - stream.spaceLeft();
	}
	
	/**
	 * 获取流实际的byte数据
	 * @return
	 */
	public byte[] getBuffer() {
		return buffer;
	}
	
	/**
	 * 获取流有效数据，用于压缩流
	 * @return
	 */
	public byte[] getRawBuffer() {
		byte[] rawData = new byte[getLength()] ;
		System.arraycopy(getBuffer(), 0, rawData, 0, getLength());
		
		return rawData ;
	}
	
	/**
	 * 获取Chunk
	 * @return
	 */
	public Chunk getChunk() {
		return new Chunk(buffer, 0, getLength());
	}
	
	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param call
	 * @throws IOException
	 */
	boolean writeCall(CallBase call) {
		//记录下写入数据前的流offset及length值 用于当写入失败后进行还原操作
		int offset = getLength();
		int length = buffer.length;
		
		try {
			write(call);
			return true;
		} catch(DistrSerilizableException e) {
			//失败时还原数据流offset到写入前
			stream = CodedOutputStream.newInstance(buffer, offset, length - offset);
			LogCore.core.error("", e);
			throw e;
		}  catch (Exception e) {
			//失败时还原数据流offset到写入前
			stream = CodedOutputStream.newInstance(buffer, offset, length - offset);
			return false;
		}
	}
	
	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param value
	 */
	@Override
	public void write(Object value) {
		try {
			writeObject(value);
		//不支持串行话的错误 要对外汇报
		} catch(DistrSerilizableException e) {
			throw e;
		} catch(OutOfSpaceException | StreamOutOfSpaceException e) {
			throw new StreamOutOfSpaceException(e, "写入数据内容过大，超过了剩余空间：len={}, used={}, value={}", lengthTotal, getLength(), value);
		} catch (Exception e) {
			throw new SysException(e, "OutputStream写入数据失败。");
		}
	}
	
	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param value
	 * @throws IOException
	 */
	private void writeObject(Object value) throws IOException {
		//空对象
		if (value == null) {
			stream.writeInt32NoTag(NULL);
			return;
		}
		
		//数据类型
		Class<?> clazz = value.getClass();
		//BYTE
		if (clazz == byte.class || clazz == Byte.class) {
			writeByte((byte)value);
		} else if (clazz == byte[].class) {
			writeByteArray((byte[])value);
		//BOOLEAN
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			writeBoolean((boolean)value);
		} else if (clazz == boolean[].class) {
			writeBooleanArray((boolean[])value);
		//INT
		} else if (clazz == int.class || clazz == Integer.class) {
			writeInt((int)value);
		} else if (clazz == int[].class) {
			writeIntArray((int[])value);
		//LONG
		} else if (clazz == long.class || clazz == Long.class) {
			writeLong((long)value);
		} else if (clazz == long[].class) {
			writeLongArray((long[])value);
		//FLOAT
		} else if (clazz == float.class || clazz == Float.class) {
			writeFloat((float)value);
		} else if (clazz == float[].class) {
			writeFloatArray((float[])value);
		//DOUBLE
		} else if (clazz == double.class || clazz == Double.class) {
			writeDouble((double)value);
		} else if (clazz == double[].class) {
			writeDoubleArray((double[])value);
		//STRING
		} else if (clazz == String.class) {
			writeString((String)value);
		} else if (clazz == String[].class) {
			writeStringArray((String[])value);
		//ENUM
		} else if (value instanceof Enum) {
			writeEnum((Enum<?>) value);
		//COLLECTION LIST SET
		} else if (value instanceof Collection) {
			//判断子类型
			int type;
			if (value instanceof List) {
				if (value instanceof LinkedList) {
					type = LINKED_lIST;
				} else {
					type = LIST;
				}
			} else if (value instanceof Set) {
				type = SET;
			} else if (value instanceof Queue) {
				type = QUEUE;
			} else {
				type = COLLECTION;
			}
			stream.writeInt32NoTag(type);
			writeCollection((Collection<?>) value);
		
		//MAP
		} else if (value instanceof Map) {
			int type;
			if (value instanceof LinkedHashMap) {
				type = LINKED_MAP;
			} else {
				type = HashMAP;
			}
			stream.writeInt32NoTag(type);
			writeNormalMap((Map<?,?>) value);

		//IDistributedSerilizable接口
		} else if (value instanceof ISerializable) {
			writeDistributed((ISerializable)value);

		//protobuf消息
		} else if (value instanceof Message) {
			writeProtoMsg((Message)value);
			
		//数组
		} else if (value instanceof Object[]) {
			writeObjectArray((Object[])value);

		//其余一律不支持
		} else {
			throw new DistrSerilizableException("发现无法被Distributed序列化的类型:{}", clazz.getName());
		}
	}
	

	
	/**
	 * 写入byte[]
	 * @param buf
	 * @param offset
	 * @param length
	 * @throws IOException
	 */
	@Override
	public void writeBytes(byte[] buf, int offset, int length) throws IOException {
		stream.writeInt32NoTag(BYTE | ARRAY);
		stream.writeInt32NoTag(length);
		stream.writeRawBytes(buf, offset, length);
	}

	//********************************************* write *********************************************//
    //********************************** 必须在逻辑上保证读写的顺序 **********************************//
	/**
	 * 写一个byte
	 * 与{@link InputStream}的readByte配对使用
	 * @param value byte值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeByte(byte value) throws IOException {
		stream.writeInt32NoTag(BYTE);
		stream.writeRawByte(value);
	}

	/**
	 * 写一个byteArray
	 * 与{@link InputStream}的readByteArray配对使用
	 * @param array byteArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeByteArray(byte[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(BYTE | ARRAY);
		stream.writeInt32NoTag(array.length);
		stream.writeRawBytes(array);
	}

	/**
	 * 写一个布尔值
	 * 与{@link InputStream}的readBoolean配对使用
	 * @param value bool值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeBoolean(boolean value) throws IOException {
		stream.writeInt32NoTag(BOOLEAN);
		stream.writeBoolNoTag(value);
	}

	/**
	 * 写一个BooleanArray
	 * 与{@link InputStream}的readBooleanArray配对使用
	 * @param array BooleanArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeBooleanArray(boolean[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(BOOLEAN | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; i++) {
			stream.writeBoolNoTag(array[i]);
		}
	}

	/**
	 * 写一个int值
	 * 与{@link InputStream}的readInt配对使用
	 * @param value int值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeInt(int value) throws IOException {
		stream.writeInt32NoTag(INT);
		stream.writeInt32NoTag(value);
	}

	/**
	 * 写一个intArray
	 * 与{@link InputStream}的readIntArray配对使用
	 * @param array intArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeIntArray(int[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(INT | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; i++) {
			stream.writeInt32NoTag(array[i]);
		}
	}

	/**
	 * 写一个long值
	 * 与{@link InputStream}的readLong配对使用
	 * @param value long值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeLong(long value) throws IOException {
		stream.writeInt32NoTag(LONG);
		stream.writeInt64NoTag(value);
	}

	/**
	 * 写一个longArray
	 * 与{@link InputStream}的readLongArray配对使用
	 * @param array longArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeLongArray(long[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(LONG | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; i++) {
			stream.writeInt64NoTag(array[i]);
		}
	}

	/**
	 * 写一个float
	 * 与{@link InputStream}的readFloat配对使用
	 * @param value float值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeFloat(float value) throws IOException {
		stream.writeInt32NoTag(FLOAT);
		stream.writeFloatNoTag(value);
	}

	/**
	 * 写一个floatArray
	 * 与{@link InputStream}的readFloatArray配对使用
	 * @param array floatArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeFloatArray(float[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(FLOAT | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; i++) {
			stream.writeFloatNoTag(array[i]);
		}
	}

	/**
	 * 写一个double
	 * 与{@link InputStream}的readDouble配对使用
	 * @param value double值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeDouble(double value) throws IOException {
		stream.writeInt32NoTag(DOUBLE);
		stream.writeDoubleNoTag(value);
	}

	/**
	 * 写一个doubleArray
	 * 与{@link InputStream}的readDoubleArray配对使用
	 * @param array doubleArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeDoubleArray(double[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(DOUBLE | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; i++) {
			stream.writeDoubleNoTag(array[i]);
		}
	}

	/**
	 * 写一个String
	 * 与{@link InputStream}的readString配对使用
	 * @param str String值
	 * @throws IOException OutOfSpaceException
	 */
	@Override
	public void writeString(String str) throws IOException {
		if (checkNull(str)) {
			return;
		}
		if (ReferenceTable.INSTANCE != null) {
			int referId = ReferenceTable.INSTANCE.getId(str);
			if (referId > 0) {
				stream.writeInt32NoTag(REFERENCE);
				stream.writeInt32NoTag(referId);
				return;
			}
		}

		stream.writeInt32NoTag(STRING);
		stream.writeStringNoTag(str);
	}

	/**
	 * 写一个stringArray
	 * 与{@link InputStream}的readStringArray配对使用
	 * @param array stringArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeStringArray(String[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(STRING | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; i++) {
			stream.writeStringNoTag(array[i]);
		}
	}

	/**
	 * 写一个enum
	 * 与{@link InputStream}的readEnum配对使用
	 * @param value enum值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeEnum(Enum<?> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(ENUM);
		stream.writeInt32NoTag(value.getClass().getName().hashCode());
		stream.writeInt32NoTag(value.ordinal());
//		stream.writeStringNoTag(value.getClass().getName());
//		stream.writeStringNoTag(value.name());
	}

	/**
	 * 写一个List(ArrayList or LinkedList)
	 * 与{@link InputStream}的readList配对使用
	 * @param value List值
	 * @throws IOException OutOfSpaceException
	 */
	public <E> void writeList(List<E> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		if (value instanceof LinkedList) {
			stream.writeInt32NoTag(LINKED_lIST);
		} else {
			stream.writeInt32NoTag(LIST);
		}
		writeCollection(value);
	}

	/**
	 * 写一个ArrayList
	 * 与{@link InputStream}的readArrayList配对使用
	 * @param value List值
	 * @throws IOException OutOfSpaceException
	 */
	public <E> void writeArrayList(ArrayList<E> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(LIST);
		writeCollection(value);
	}

	/**
	 * 写一个linkedList
	 * 与{@link InputStream}的readLinkedList配对使用
	 * @param value linkedList值
	 * @throws IOException OutOfSpaceException
	 */
	public <E> void writeLinkedList(LinkedList<E> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(LINKED_lIST);
		writeCollection(value);
	}

	/**
	 * 写一个Set
	 * 与{@link InputStream}的readSet配对使用
	 * @param value Set值
	 * @throws IOException OutOfSpaceException
	 */
	public <E> void writeSet(Set<E> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(SET);
		writeCollection(value);
	}

	/**
	 * 写一个Queue
	 * 与{@link InputStream}的readQueue配对使用
	 * @param value Queue值
	 * @throws IOException OutOfSpaceException
	 */
	public <E> void writeQueue(Queue<E> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(QUEUE);
		writeCollection(value);
	}

	private <E> void writeCollection(Collection<E> value) throws IOException {
		stream.writeInt32NoTag(value.size());
		for (Object o : value) {
			this.write(o);
		}
	}

	/**
	 * 写一个Map
	 * 与{@link InputStream}的readMap配对使用
	 * @param value Map值
	 * @throws IOException OutOfSpaceException
	 */
	public <K, V> void writeHashMap(Map<K, V> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(HashMAP);
		writeNormalMap(value);
	}

	/**
	 * 写一个linkedHashMap
	 * 与{@link InputStream}的readLinkedHashMap配对使用
	 * @param value Map值
	 * @throws IOException OutOfSpaceException
	 */
	public <K, V> void writeLinkedHashMap(Map<K, V> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		stream.writeInt32NoTag(LINKED_MAP);
		writeNormalMap(value);
	}

	public <K, V> void writeMap(Map<K, V> value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		int type;
		if (value instanceof LinkedHashMap) {
			type = LINKED_MAP;
		} else {
			type = HashMAP;
		}
		stream.writeInt32NoTag(type);
		writeNormalMap(value);
	}

	/**
	 * 写一个Map
	 * 与{@link InputStream}的readNormalMap配对使用
	 * @param value Map值
	 * @throws IOException OutOfSpaceException
	 */
	private <K, V> void writeNormalMap(Map<K, V> value) throws IOException {
		stream.writeInt32NoTag(value.size());
		for (Entry<K, V> e : value.entrySet()) {
			this.write(e.getKey());
			this.write(e.getValue());
		}
	}

	/**
	 * 写一个ISerilizable
	 * 与{@link InputStream}的readDistributed配对使用
	 * @param value ISerilizable值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeDistributed(ISerializable value) throws IOException {
		if (checkNull(value)) {
			return;
		}
		int id = value.getClass().getName().hashCode();
		stream.writeInt32NoTag(DISTRIBUTED);
		stream.writeInt32NoTag(id);
		Function2WithException<OutputStream, ISerializable, IOException> func = serializeWriteFuncMap.get(id);
		if (func == null) {
			throw new SysException("未能找到序列化对象, hashCode: {}", id);
		}
		func.apply(this, value);
	}

	/**
	 * 写一个protoMsg
	 * 与{@link InputStream}的readProtoMsg配对使用
	 * @param msg Message值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeProtoMsg(Message msg) throws IOException {
		if (checkNull(msg)) {
			return;
		}
		stream.writeInt32NoTag(MSG);
		byte[] bytes = msg.toByteArray();
		/** 消息长度 不包括消息类型 */
		stream.writeInt32NoTag(bytes.length);
		stream.writeInt32NoTag(msg.getClass().getName().hashCode());
		stream.writeRawBytes(bytes);
	}

	/**
	 * 写一个ObjectArray
	 * 与{@link InputStream}的readObjectArray配对使用
	 * @param array ObjectArray值
	 * @throws IOException OutOfSpaceException
	 */
	public void writeObjectArray(Object[] array) throws IOException {
		if (checkNull(array)) {
			return;
		}
		stream.writeInt32NoTag(OBJECT | ARRAY);
		stream.writeInt32NoTag(array.length);
		for (int i = 0; i < array.length; ++i) {
			this.write(array[i]);
		}
	}

	/**
	 * 判断一个值是否为null，为null写0，否则写1
	 * @param obj 将要写入的对象
	 * @throws IOException OutOfSpaceException
	 */
	public void writeNull(Object obj) throws IOException {
		if (obj == null) {
			stream.writeInt32NoTag(NULL);
		} else {
			stream.writeInt32NoTag(1);
		}
	}

	/**
	 * 检查是否为空
	 * @param obj 要检查的对象
	 * @return 对象是否未空
	 */
	private boolean checkNull(Object obj) throws IOException{
		if (null == obj) {
			stream.writeInt32NoTag(NULL);
			return true;
		}
		return false;
	}

	@Override
	public void writeShort(short i) throws IOException {

	}

	public static void registerSerializeWriteFunc(int key, Function2WithException<OutputStream, ISerializable, IOException> func){
		Function2WithException<OutputStream, ISerializable, IOException> oldFunc = serializeWriteFuncMap.put(key, func);
		if (oldFunc != null){
			throw new SysException("register repeated serialize func");
		}
	}
}