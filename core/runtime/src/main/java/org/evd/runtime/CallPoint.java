package org.evd.runtime;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CallPoint {
    public final String nodeId;
    public final Object servId;

    /**
     * 构造函数
     * @param nodeId
     * @param servId
     */
    public CallPoint(String nodeId, Object servId) {
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
}
