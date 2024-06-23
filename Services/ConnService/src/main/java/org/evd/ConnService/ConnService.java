package org.evd.ConnService;

import org.evd.annotation.Rpc;
import org.evd.annotation.ServiceClass;
import org.evd.runtime.CallPoint;
import org.evd.runtime.Node;
import org.evd.common.proxy.StageServiceProxy;
import org.evd.runtime.Service;
import org.evd.runtime.support.Log;

@ServiceClass
public class ConnService extends Service {
    public ConnService(Node node, String name, String scheduledName) {
        super(node, name, scheduledName);
    }

    @Override
    public void init() {
        Log.console("ConnService Init");
    }

    @Override
    public void tick() {
//        StageServiceProxy.inst().doSome1(1, 2);
        CallPoint callPoint = new CallPoint("node1", "stage1");
        String s = StageServiceProxy.inst(callPoint).doSome3(1);
        Log.console("ConnService tick reveive {" + s + "}");
    }

    @Rpc
    public void con(){

    }

    @Rpc
    public void con1(){

    }
}
