package org.evd.runtime;

import org.evd.runtime.support.TwoTuple;

/**
 * @author
 *
 * RPC代理类的基类
 */
public abstract class RPCProxyBase {
	protected CallPoint remote;
	/**
	 * 异步注册监听RPC返回
	 * @param method
	 * @param context
	 */
//	public abstract void listenResult(Function2<Param, Param> method, Object...context);
//	public abstract void listenResult(Function3<Boolean, Param, Param> method, Object...context);
	
	/**
	 * 同步等待RPC返回
	 * @return
	 */
//	public abstract Param waitForResult();

	public TwoTuple<Integer, String[]> httpActionCall(String action){
		return null;
	}

	public String getId(){
		return null;
	}

	public CallPoint getRemote(){
		return remote;
	}
}
