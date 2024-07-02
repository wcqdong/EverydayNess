package org.evd.game.runtime.call;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;
import org.evd.game.base.ISerializable;

@Serializable
public class CallPoint implements ISerializable {
    @SerializerField
    public String nodeId;
    @SerializerField
    public String servId;

    public CallPoint(){

    }

    /**
     * 构造函数
     * @param nodeId
     * @param servId
     */
    public CallPoint(String nodeId, String servId) {
        this.nodeId = nodeId;
        this.servId = servId;
    }

    public CallPoint(CallPoint callPoint) {
        this.nodeId = callPoint.nodeId;
        this.servId = callPoint.servId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("nodeId", nodeId)
                .append("servId", servId)
                .toString();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getServId() {
        return servId;
    }

    public void setServId(String servId) {
        this.servId = servId;
    }
}
