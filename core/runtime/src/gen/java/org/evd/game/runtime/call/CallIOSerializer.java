package org.evd.game.runtime.call;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class CallIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 * @key 属性名称
	 */
	public static void write(OutputStream out, Call instance) throws IOException {
		org.evd.game.runtime.call.CallBaseIOSerializer.write(out, instance);
		out.writeInt(instance.getMethodKey());
		out.writeObjectArray(instance.getMethodParam());
		out.writeBoolean(instance.isNeedResult());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, Call instance) throws IOException {
		org.evd.game.runtime.call.CallBaseIOSerializer.read(in, instance);
		instance.setMethodKey(in.readInt());
		instance.setMethodParam(in.readObjectArray());
		instance.setNeedResult(in.readBoolean());
	}
}