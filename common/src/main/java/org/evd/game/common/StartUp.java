package org.evd.game.common;

import org.evd.game.runtime.Node;
import org.evd.game.runtime.annotation.Module;

@Module
public class StartUp {

    @Module.OnStart(priority = 0)
    public static void Start(Node node){
        SerializerRegister.register();
    }
    @Module.OnEnd(priority = 1000)
    public static void End(Node node){
    }
}
