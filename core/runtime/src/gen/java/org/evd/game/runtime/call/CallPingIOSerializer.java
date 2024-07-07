package org.evd.game.runtime.call;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class CallPingIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 */
	public static void write(OutputStream out, CallPing instance) throws IOException {
		org.evd.game.runtime.call.CallBaseIOSerializer.write(out, instance);
		out.writeString(instance.getAddr());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, CallPing instance) throws IOException {
		org.evd.game.runtime.call.CallBaseIOSerializer.read(in, instance);
		instance.setAddr(in.readString());
	}
}