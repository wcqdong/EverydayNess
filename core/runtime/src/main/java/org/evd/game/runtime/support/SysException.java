package org.evd.game.runtime.support;

/**
 * 
 * 
 * 当程序出现系统级错误，不希望用户看到出错信息的时候抛出此异常
 */
public class SysException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public SysException(String str) {
		super(str);
	}
	
	public SysException(Throwable e) {
		super(e);
	}
	
	public SysException(Throwable e, String str) {
		super(str, e);
	}
	
	public SysException(String str, Object...params) {
		super(Utils.createStr(str, params));
	}
	
	public SysException(Throwable e, String str, Object...params) {
		super(Utils.createStr(str, params), e);
	}
}