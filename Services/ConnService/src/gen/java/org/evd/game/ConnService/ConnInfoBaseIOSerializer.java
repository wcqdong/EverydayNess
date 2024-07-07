package org.evd.game.ConnService;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class ConnInfoBaseIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 */
	public static void write(OutputStream out, ConnInfoBase instance) throws IOException {
		out.writeInt(instance.getCon1());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, ConnInfoBase instance) throws IOException {
		instance.setCon1(in.readInt());
	}
}