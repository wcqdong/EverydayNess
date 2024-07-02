package org.evd.game.runtime.call;

import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;

@Serializable
public class Call extends CallBase {
    /** to service后调用哪个方法 */
    @SerializerField
    public int methodKey;
    /** to service后调用方法的参数 */
    @SerializerField
    public Object[] methodParam;
    /** to service后调用的方法是否有返回值 */
    @SerializerField
    public boolean needResult;

    public CallResult createReturn() {
        CallResult callResult = new CallResult();

        callResult.from = new CallPoint(this.to);
        callResult.to = new CallPoint(this.from);
        callResult.id = this.id;

        return callResult;
    }

    public int getMethodKey() {
        return methodKey;
    }

    public void setMethodKey(int methodKey) {
        this.methodKey = methodKey;
    }

    public Object[] getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(Object[] methodParam) {
        this.methodParam = methodParam;
    }

    public boolean isNeedResult() {
        return needResult;
    }

    public void setNeedResult(boolean needResult) {
        this.needResult = needResult;
    }
}
