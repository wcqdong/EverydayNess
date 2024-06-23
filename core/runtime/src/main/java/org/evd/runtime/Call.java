package org.evd.runtime;

public class Call extends CallBase{
    /** to service后调用哪个方法 */
    public int methodKey;
    /** to service后调用方法的参数 */
    public Object[] methodParam;
    /** to service后调用的方法是否有返回值 */
    public boolean needResult;

    public CallResult createReturn() {
        CallResult callResult = new CallResult();

        callResult.from = new CallPoint(this.to);
        callResult.to = new CallPoint(this.from);
        callResult.continuationId = this.continuationId;
        callResult.debugSn = this.debugSn;

        return callResult;
    }
}
