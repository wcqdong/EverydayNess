package org.evd.game.ConnService;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class ConnInfoIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 * @key 属性名称
	 */
	public static void write(OutputStream out, ConnInfo instance) throws IOException {
		org.evd.game.ConnService.ConnInfoBaseIOSerializer.write(out, instance);
		out.writeInt(instance.getA());
		out.writeLong(instance.getLongValue());
		out.writeString(instance.getStringValue());
		out.writeIntArray(instance.getIntArrayValue());
		out.writeStringArray(instance.getStringArrayValue());
		out.writeList(instance.getList());
		out.writeList(instance.getList1());
		out.writeList(instance.getList2());
		out.writeMap(instance.getMap());
		out.writeMap(instance.getMap1());
		out.writeMap(instance.getMap2());
		out.writeSet(instance.getSet1());
		out.writeSet(instance.getSet2());
		out.writeSet(instance.getSet3());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, ConnInfo instance) throws IOException {
		org.evd.game.ConnService.ConnInfoBaseIOSerializer.read(in, instance);
		instance.setA(in.readInt());
		instance.setLongValue(in.readLong());
		instance.setStringValue(in.readString());
		instance.setIntArrayValue(in.readIntArray());
		instance.setStringArrayValue(in.readStringArray());
		instance.setList(in.readList());
		instance.setList1(in.readList());
		instance.setList2(in.readList());
		instance.setMap(in.readMap());
		instance.setMap1(in.readMap());
		instance.setMap2(in.readMap());
		instance.setSet1(in.readSet());
		instance.setSet2(in.readSet());
		instance.setSet3(in.readSet());
	}
}