package org.evd.game.runtime.call;

import org.evd.game.annotation.SerializeClass;
import org.evd.game.annotation.SerializeField;

@SerializeClass
public class CallResult extends CallBase {
    @SerializeField
    public Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
