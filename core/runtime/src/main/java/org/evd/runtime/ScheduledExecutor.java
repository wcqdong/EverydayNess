package org.evd.runtime;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduledExecutor extends ScheduledThreadPoolExecutor {
    private final String name;
    public ScheduledExecutor(String name, int corePoolSize) {
        super(corePoolSize);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
