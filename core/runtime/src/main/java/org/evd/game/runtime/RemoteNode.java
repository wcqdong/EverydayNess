package org.evd.game.runtime;

import org.evd.game.runtime.call.CallBase;
import org.evd.game.runtime.call.CallPing;
import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.serialize.OutputStream;
import org.evd.game.runtime.support.LogCore;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 *
 * 远程Node
 */
public class RemoteNode {
	/** 连接检测时间间隔 3秒 */
	public static final long INTERVAL_PING = 3000;
	/** 连接丢失时间间隔 8秒 */
	public static final long INTERVAL_LOST = 8000;
	
	/** 远程Node名称 */
	private final String remoteId;
	/** 远程Node地址 */
	private final String remoteAddr;
	/** 本地Node名称 */
	private final Node localNode;
	
	/** ZMQ上下文 */
	private final ZContext zmqContext;
	/** ZMQ连接 */
	private final ZMQ.Socket zmqPush;
	
	/** 最后一次收到连接检查时间 */
	private long lastRecvPingTime = 0;
	
	/** 是否连接上 */
	private boolean connected;
	
	/**
	 * 构造函数
	 * @param localNode
	 * @param remoteName
	 * @param remoteAddr
	 */
	public RemoteNode(Node localNode, String remoteName, String remoteAddr) {
		this.localNode = localNode;
		this.remoteId = remoteName;
		this.remoteAddr = remoteAddr;
		
		this.zmqContext = new ZContext();
		this.zmqPush = zmqContext.createSocket(SocketType.PUSH);
		this.zmqPush.setLinger(3000);
		// 默认不限制消息缓存数量(可能会导致占用过多内存)
		this.zmqPush.setSndHWM(0);
		this.zmqPush.setReconnectIVL(2000);
		this.zmqPush.setReconnectIVLMax(5000);
		this.zmqPush.connect(remoteAddr);
	}
	
	/**
	 * 心跳操作
	 */
	public void pulse() {
		// 当前时间
		long timeCurr = localNode.getTimeCurrent();
		
		// 到达间隔时间后 进行连接检测
		ping();
		
		// 活跃状态下 长时间没收到心跳检测 那么就认为连接已丢失
		if (isActive() && (timeCurr - lastRecvPingTime) > RemoteNode.INTERVAL_LOST) {
			connected = false;
			LogCore.remote.error("失去与远程Node的连接：name={}, addr={}", remoteId, remoteAddr);
		}
	}
	
	/**
	 * 进行连接测试
	 */
	public void ping() {
		// 创建并发送测试请求
		CallPing call = new CallPing();
		call.from = new CallPoint(localNode.getName(), null);
		call.to = new CallPoint(remoteId, null);
		call.addr = localNode.getAddr();

		sendCall(call);
	}
	
	/**
	 * 处理连接测试请求
	 */
	public void pingHandle() {
		// 非活跃的情况下收到连接测试
		if (!isActive()) {
			LogCore.remote.info("远程Node激活：id={}, addr={}", remoteId, remoteAddr);
		}
		
		// 设置最后收到连接测试的时间
		lastRecvPingTime = localNode.getTimeCurrent();

		// 设置为已连接状态
		if (!connected) {
			connected = true;
		}
	}
	
	/**
	 * 是否为活跃状态
	 * @return
	 */
	public boolean isActive() {
		return connected && lastRecvPingTime > 0;
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		synchronized (zmqPush) {
			zmqPush.close();
		}
		
		zmqContext.destroy();
	}
	
	/**
	 * 发送调用请求
	 * @param call
	 */
	public void sendCall(CallBase call) {
		// 创建输出流并写入
		try(OutputStream out = new OutputStream()) {
			out.write(call);

			// 数据拷贝出来
			byte[] copy = new byte[out.getLength()];
			System.arraycopy(out.getBuffer(), 0, copy, 0, out.getLength());
			// 发送消息
			send(copy);
		}
	}
	
	/**
	 * 发送调用请求
	 * 调用此方法的只有node线程，所以可以不用加锁
	 * 考虑到如果用port线程发送，由于处理时间长可能导致竞争激烈，所以在之前就把buf交给了node线程，由note线程统一发送
	 *
	 * @param buf buf必须是提前copy过的
	 */
	public void send(byte[] buf) {
		//（zmq内部不是线程安全的，必须做同步发送）
//		synchronized (zmqPush) {
//			zmqPush.send(buf, 0, size, 0);
//		}
		zmqPush.send(buf, 0);
	}

	public String getRemoteId() {
		return remoteId;
	}
}
