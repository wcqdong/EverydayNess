package org.evd.game.runtime;

import org.evd.game.runtime.serialize.InputStream;
import org.evd.game.runtime.serialize.OutputStream;
import java.io.IOException;


public final class TickTimerIOSerializer{
	/**
	 * 序列化
	 * @param out 输出流
	 * @param instance 实例
	 * @key 属性名称
	 */
	public static void write(OutputStream out, TickTimer instance) throws IOException {
		out.writeBoolean(instance.isRunning());
		out.writeLong(instance.getInterval());
		out.writeLong(instance.getNextTime());
		out.writeLong(instance.getStartTime());
	}
	
	/**
	 * 反序列化
	 * @param in 输入流
	 * @param instance 实例
	 */
	public static void read(InputStream in, TickTimer instance) throws IOException {
		instance.setRunning(in.readBoolean());
		instance.setInterval(in.readLong());
		instance.setNextTime(in.readLong());
		instance.setStartTime(in.readLong());
	}
}