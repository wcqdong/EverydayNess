package org.evd.game.runtime.call;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class CallBaseIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 * @key 属性名称
	 */
	public static void write(OutputStream out, CallBase instance) throws IOException {
		org.evd.game.runtime.call.CallPointIOSerializer.write(out, instance.getFrom());
		org.evd.game.runtime.call.CallPointIOSerializer.write(out, instance.getTo());
		out.writeLong(instance.getId());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, CallBase instance) throws IOException {
		org.evd.game.runtime.call.CallPoint from = new org.evd.game.runtime.call.CallPoint();
		org.evd.game.runtime.call.CallPointIOSerializer.read(in, from);
		instance.setFrom(from);
		org.evd.game.runtime.call.CallPoint to = new org.evd.game.runtime.call.CallPoint();
		org.evd.game.runtime.call.CallPointIOSerializer.read(in, to);
		instance.setTo(to);
		instance.setId(in.readLong());
	}
}