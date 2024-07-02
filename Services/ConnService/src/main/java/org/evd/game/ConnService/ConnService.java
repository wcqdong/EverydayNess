package org.evd.game.ConnService;

import org.evd.game.annotation.Rpc;
import org.evd.game.annotation.ServiceClass;
import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.Node;
import org.evd.game.common.proxy.StageServiceProxy;
import org.evd.game.runtime.Service;
import org.evd.game.runtime.support.LogCore;

@ServiceClass
public class ConnService extends Service {
    public ConnService(Node node, String name, String scheduledName) {
        super(node, name, scheduledName);
    }

    @Override
    public void init() {
        LogCore.core.info("ConnService Init");
    }

    @Override
    public void tick() {
//        StageServiceProxy.inst().doSome1(1, 2);
        CallPoint callPoint = new CallPoint("node1", "stage1");
        String s = StageServiceProxy.inst(callPoint).doSome3(1);
        LogCore.core.info("ConnService tick reveive {" + s + "}");
    }

    @Rpc
    public void con(){

    }

    @Rpc
    public void con1(){

    }

    @Rpc
    public void con2(){

    }
}
