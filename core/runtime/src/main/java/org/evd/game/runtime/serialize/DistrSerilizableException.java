package org.evd.game.runtime.serialize;

import org.evd.game.runtime.support.RuntimeUtils;

/**
 * 
 * 
 * 当输出流发现不能串行化的对象后会抛出此异常
 */
public class DistrSerilizableException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public DistrSerilizableException(String str, Object...params) {
		super(RuntimeUtils.createStr(str, params));
	}
}