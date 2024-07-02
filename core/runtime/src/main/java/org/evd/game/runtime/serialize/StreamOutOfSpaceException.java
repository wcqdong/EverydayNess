package org.evd.game.runtime.serialize;


import org.evd.game.runtime.support.Utils;

/**
 * 
 * 
 * 当流写入空间不足时会抛出本异常
 */
public class StreamOutOfSpaceException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public StreamOutOfSpaceException(Throwable e, String str, Object...params) {
		super(createStr(str, params), e);
	}
	
	/**
	 * 创建输出字符串
	 * 如果输出内容过长 只保留前200个字符
	 * @param str
	 * @param params
	 * @return
	 */
	private static String createStr(String str, Object...params) {
		String msg = Utils.createStr(str, params);
		
		if (msg.length() > 500) {
			return msg.substring(0, 500) + "...";
		} else {
			return msg;
		}
	}
}