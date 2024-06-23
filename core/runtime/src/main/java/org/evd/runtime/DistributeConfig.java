package org.evd.runtime;

import java.util.concurrent.ConcurrentHashMap;

public class DistributeConfig {
    private final static ConcurrentHashMap<String, CallPoint> singletonService2Node = new ConcurrentHashMap<>();

    public static CallPoint getNode(String serviceName) {
        return singletonService2Node.get(serviceName);
    }
}
