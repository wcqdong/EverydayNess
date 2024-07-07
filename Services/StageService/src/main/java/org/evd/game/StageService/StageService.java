package org.evd.game.StageService;

import org.evd.game.annotation.Actor;
import org.evd.game.common.proxy.ConnServiceProxy;
import org.evd.game.runtime.Node;
import org.evd.game.annotation.Rpc;
import org.evd.game.runtime.Service;
import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.support.LogCore;
import org.evd.game.runtime.support.RuntimeUtils;

@Actor
public class StageService extends Service {
    public int a;
    public StageService(Node node, String name, String scheduledName) {
        super(node, name, scheduledName);
    }
    public StageService(Node node, String name, String scheduledName, int interval) {
        super(node, name, scheduledName, interval);
    }

    @Rpc
    public String doSome1(int a, Integer b) {
        String str = RuntimeUtils.createStr("{}::{}::doSome1()", node.getId(), id);
        System.out.println(str);
        LogCore.core.info(str);

        ConnServiceProxy proxy = ConnServiceProxy.inst(new CallPoint("node1", "conn1"));
        String result = proxy.con();
        System.out.println("receive = " + result);

        return str;

    }

    @Rpc
    public void doSome2(Integer a, Integer b) {
        LogCore.core.info("StageService doSome2()");
    }

    @Rpc
    public String doSome3(Integer a) {
        System.out.println("StageService doSome3()");
//        LogCore.core.info("StageService doSome3()");
        return "from StageService doSome3";
    }
}
