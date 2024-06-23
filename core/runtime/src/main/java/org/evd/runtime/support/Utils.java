package org.evd.runtime.support;


import org.apache.logging.log4j.message.ParameterizedMessage;

public class Utils {
    public static String createStr(String str, Object... params) {
        return ParameterizedMessage.format(str, params);
    }
}
