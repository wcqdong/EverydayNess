package org.evd.game.runtime.call;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class CallPointIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 */
	public static void write(OutputStream out, CallPoint instance) throws IOException {
		out.writeString(instance.getNodeId());
		out.writeString(instance.getServId());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, CallPoint instance) throws IOException {
		instance.setNodeId(in.readString());
		instance.setServId(in.readString());
	}
}