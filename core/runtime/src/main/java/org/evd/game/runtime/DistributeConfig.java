package org.evd.game.runtime;

import org.evd.game.runtime.call.CallPoint;

import java.util.concurrent.ConcurrentHashMap;

public class DistributeConfig {
    private final static ConcurrentHashMap<String, CallPoint> singletonService2Node = new ConcurrentHashMap<>();

    public static CallPoint getNode(String serviceName) {
        return singletonService2Node.get(serviceName);
    }

    public static void addSingleService(Service service) {
        singletonService2Node.put(service.getName(), new CallPoint(service.getNode().getName(), service.getName()));
    }
}
