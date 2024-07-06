package org.evd.game.StageService;

import org.evd.game.annotation.Actor;
import org.evd.game.runtime.Node;
import org.evd.game.annotation.Rpc;
import org.evd.game.runtime.Service;
import org.evd.game.runtime.support.LogCore;

@Actor
public class StageService extends Service {
    public int a;
    public StageService(Node node, String name, String scheduledName) {
        super(node, name, scheduledName);
    }

    @Rpc
    public void doSome1(int a, Integer b) {
        LogCore.core.info("StageService doSome1()");
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
