package org.evd.game.common.proxy;

import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.RPCProxyBase;
import org.evd.game.runtime.Service;

/**
* 根据StageServiceService生成的代理类
*/
public class StageServiceProxy extends RPCProxyBase {

    public final static class EnumCall{
        public final static int ENUM_VOID_DOSOME1_INT_INT = 0;
        public final static int ENUM_VOID_DOSOME2_INT_INT = 1;
        public final static int ENUM_STRING_DOSOME3_INT = 2;
    }

    private StageServiceProxy(CallPoint callPoint){
        this.remote = callPoint;
    }
    public static StageServiceProxy inst(CallPoint callPoint) {
        return new StageServiceProxy(callPoint);
    }

    public void doSome1(int a, int b){
        Service service = Service.getCurrent();
        service.call(remote, EnumCall.ENUM_VOID_DOSOME1_INT_INT, new Object[]{a, b});
    }
    public void doSome2(int a, int b){
        Service service = Service.getCurrent();
        service.call(remote, EnumCall.ENUM_VOID_DOSOME2_INT_INT, new Object[]{a, b});
    }
    public String doSome3(int a){
        Service service = Service.getCurrent();
        return (String)service.callWait(remote, EnumCall.ENUM_STRING_DOSOME3_INT, new Object[]{a});
    }
}
