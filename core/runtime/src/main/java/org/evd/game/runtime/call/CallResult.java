package org.evd.game.runtime.call;

import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;

@Serializable
public class CallResult extends CallBase {
    @SerializerField
    public Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
