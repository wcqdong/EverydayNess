package org.evd.game.runtime;

/**
 * @author
 *
 * RPC实现类的基类
 */
public abstract class RPCImplBase {	
	/**
	 * 根据函数id获取service上的RPC函数
	 * @param serv
	 * @param methodKey
	 * @return
	 */
	public abstract Object getMethodFunction(Service serv, int methodKey);
}
