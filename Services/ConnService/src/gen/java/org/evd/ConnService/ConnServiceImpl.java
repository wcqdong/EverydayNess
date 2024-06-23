package org.evd.ConnService;

import org.evd.runtime.RPCImplBase;
import org.evd.runtime.Service;
import org.evd.runtime.support.function.*;

/**
* 根据ConnServiceService生成的rpc分发类
*/
public class ConnServiceImpl extends RPCImplBase {
    public final static class EnumCall{
        public final static int ENUM_VOID_CON = 0;
        public final static int ENUM_VOID_CON1 = 1;
    }

    @Override
    public Object getMethodFunction(Service serv, int methodKey) {
        ConnService service = (ConnService) serv;
        switch (methodKey){
            case EnumCall.ENUM_VOID_CON:
                return (Function0)service::con;
            case EnumCall.ENUM_VOID_CON1:
                return (Function0)service::con1;
            default:
                return null;
        }
    }
}
