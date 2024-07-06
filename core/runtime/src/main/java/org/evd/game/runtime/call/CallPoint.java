package org.evd.game.runtime.call;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.evd.game.annotation.SerializeClass;
import org.evd.game.annotation.SerializeField;
import org.evd.game.base.ISerializable;

@SerializeClass
public class CallPoint implements ISerializable {
    @SerializeField
    public String nodeId;
    @SerializeField
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
