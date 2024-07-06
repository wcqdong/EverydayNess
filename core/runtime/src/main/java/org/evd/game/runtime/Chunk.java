package org.evd.game.runtime;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import org.evd.game.annotation.SerializeClass;
import org.evd.game.base.ISerializable;
import org.evd.game.base.InputStreamBase;
import org.evd.game.base.OutputStreamBase;

import java.io.IOException;

/**
 * 
 * 
 * 当输入流与输出流转换时，可以用本对象作为中间类型。
 */
@SerializeClass(customized = true)
public class Chunk implements ISerializable {
	public byte[] buffer;
	public int offset;
	public int length;
	
	public Chunk() {}
	
	public Chunk(Builder msg) {
		this(msg.build());
	}
	
	public Chunk(Message msg) {
		this(msg.toByteArray());
	}
	
	public Chunk(byte[] buf) {
		buffer = buf;
		offset = 0;
		length = buf.length;
	}
	
	public Chunk(byte[] buf, int off, int len) {
		buffer = buf;
		offset = off;
		length = len;
	}

	@Override
	public void writeTo(OutputStreamBase stream) throws IOException {
		stream.write(this.length);
		stream.writeBytes(buffer, offset, length);
	}

	@Override
	public void readFrom(InputStreamBase stream) throws IOException {
		this.length = stream.read();
		this.offset = 0;
		this.buffer = stream.read();
	}
}
