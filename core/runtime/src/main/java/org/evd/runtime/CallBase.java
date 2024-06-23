package org.evd.runtime;

public class CallBase {
    /** ------------从哪来-------------*/
    public CallPoint from;
    /** ------------到哪去-------------*/
    public CallPoint to;
    /** 请求后回调contextid */
    public long continuationId;
    /** 这个啥也不是，序列号，debug用的 */
    public int debugSn;
}
