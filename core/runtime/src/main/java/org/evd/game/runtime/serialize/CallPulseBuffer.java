package org.evd.game.runtime.serialize;

import org.evd.game.runtime.call.CallBase;
import org.evd.game.runtime.Node;
import org.evd.game.runtime.Service;

import java.io.IOException;

/**
 *
 * 
 * 请求缓冲
 */
public class CallPulseBuffer implements AutoCloseable{
	/** 目标Node名称 */
	private String targetNodeId;
	/** 缓冲 */
	private OutputStream buffer = new OutputStream();
	
	/**
	 * 构造函数
	 * @param targetNodeId
	 */
	public CallPulseBuffer(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}
	
	/**
	 * 写入新请求
     */
	public boolean writeCall(Service service, CallBase call) {
		// rpc参数不可变，可以一直引用
		if(call.immutable) {
            try {
                buffer.writeLong(call.id);
				service.addCallFrameReferences(call);
			} catch (IOException e) {
				// 不会有错
				return true;
            }
			return false;
		}
		return buffer.writeCall(call);
	}

	/**
	 * 刷新缓冲区
	 * @param node
	 */
	public void flush(Node node) {
		if (buffer.getLength() == 0) {
			return;
		}
		
		try {
			node.flushCall(targetNodeId, buffer.getBuffer(), buffer.getLength());
		} finally {
			buffer.reset();
		}
	}
	
	/**
	 * 缓冲区是否有未发送数据
	 * @return
	 */
	public boolean isEmpty() {
		return buffer.getLength() == 0;
	}
	
	/**
	 * 获取已使用长度
	 * @return
	 */
	public int getLength() {
		return buffer.getLength();
	}

	@Override
	public void close() {
		buffer.close();
	}
}
