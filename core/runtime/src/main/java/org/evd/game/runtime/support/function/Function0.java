package org.evd.game.runtime.support.function;

/**
 * 接受0个参数的函数
 */
@FunctionalInterface
public interface Function0 {
	
	void apply() throws InterruptedException;

}
