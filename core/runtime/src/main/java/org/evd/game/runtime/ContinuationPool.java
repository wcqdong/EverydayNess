package org.evd.game.runtime;

import java.util.ArrayList;
import java.util.List;

public class ContinuationPool {
    private final Service service;
    private final List<Task.ContinuationWrapper> pool = new ArrayList<>();
    public ContinuationPool(Service service){
        this.service = service;
    }

    public Task.ContinuationWrapper apply(){
        if (pool.isEmpty())
            return new Task.ContinuationWrapper(service);
        else
            return pool.removeLast();
    }

    public void recycle(Task.ContinuationWrapper callBack) {
        // 清理
        callBack.close();
        // 回收
        pool.add(callBack);
    }
}
