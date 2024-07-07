package org.evd.game.runtime.support;


import org.apache.logging.log4j.message.ParameterizedMessage;

public class RuntimeUtils {
    /**
     * 基于参数创建字符串
     * #0开始
     *
     * @param str
     * @param params
     * @return
     */
    public static String createStr(String str, Object... params) {
        return ParameterizedMessage.format(str, params);
    }
}
