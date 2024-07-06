package org.evd.game.ConnService;

import org.evd.game.runtime.Node;
import org.evd.game.runtime.annotation.Module;

@Module
public class StartUp {

    @Module.OnStart
    public static void Start(Node node){
        SerializerRegister.register();;
    }

    @Module.OnEnd
    public static void End(Node node){
    }
}
