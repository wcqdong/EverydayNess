package org.evd.game.ConnService;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class CoInfoIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 * @key 属性名称
	 */
	public static void write(OutputStream out, CoInfo instance) throws IOException {
		out.writeString(instance.getAa());
		out.writeList(instance.getList());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, CoInfo instance) throws IOException {
		instance.setAa(in.readString());
		instance.setList(in.readList());
	}
}