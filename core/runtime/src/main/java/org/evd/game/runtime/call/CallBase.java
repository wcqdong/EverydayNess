package org.evd.game.runtime.call;

import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;
import org.evd.game.base.ISerializable;

@Serializable
public abstract class CallBase implements ISerializable {
    /** ------------从哪来-------------*/
    @SerializerField
    public CallPoint from;
    /** ------------到哪去-------------*/
    @SerializerField
    public CallPoint to;
    /** 请求后回调contextid */
    @SerializerField
    public long id;
    /** 参数是不变的，则可以不序列化*/
    public boolean immutable;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CallPoint getFrom() {
        return from;
    }

    public void setFrom(CallPoint from) {
        this.from = from;
    }

    public CallPoint getTo() {
        return to;
    }

    public void setTo(CallPoint to) {
        this.to = to;
    }
}
