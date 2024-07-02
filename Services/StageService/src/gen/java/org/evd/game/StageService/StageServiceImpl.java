package org.evd.game.StageService;

import org.evd.game.runtime.RPCImplBase;
import org.evd.game.runtime.Service;
import org.evd.game.runtime.support.function.*;

/**
* 根据StageServiceService生成的rpc分发类
*/
public class StageServiceImpl extends RPCImplBase {
    public final static class EnumCall{
        public final static int ENUM_VOID_DOSOME1_INT_INT = 0;
        public final static int ENUM_VOID_DOSOME2_INT_INT = 1;
        public final static int ENUM_STRING_DOSOME3_INT = 2;
    }

    @Override
    public Object getMethodFunction(Service serv, int methodKey) {
        StageService service = (StageService) serv;
        switch (methodKey){
            case EnumCall.ENUM_VOID_DOSOME1_INT_INT:
                return (Function2<Integer, Integer>)service::doSome1;
            case EnumCall.ENUM_VOID_DOSOME2_INT_INT:
                return (Function2<Integer, Integer>)service::doSome2;
            case EnumCall.ENUM_STRING_DOSOME3_INT:
                return (ReturnFunction1<String, Integer>)service::doSome3;
            default:
                return null;
        }
    }
}
