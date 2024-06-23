package org.evd.StageService;

import org.evd.annotation.ServiceClass;
import org.evd.runtime.Node;
import org.evd.annotation.Rpc;
import org.evd.runtime.Service;
import org.evd.runtime.support.Log;

@ServiceClass
public class StageService extends Service {
    public int a;
    public StageService(Node node, String name, String scheduledName) {
        super(node, name, scheduledName);
    }

    @Rpc
    public void doSome1(int a, Integer b) {
        Log.console("StageService doSome1()");
    }

    @Rpc
    public void doSome2(Integer a, Integer b) {
        Log.console("StageService doSome2()");
    }

    @Rpc
    public String doSome3(Integer a) {
        Log.console("StageService doSome3()");
        return "from StageService doSome3";
    }
}
