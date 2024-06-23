package org.evd.common.proxy;

import org.evd.runtime.CallPoint;
import org.evd.runtime.RPCProxyBase;
import org.evd.runtime.Service;

/**
* 根据StageServiceService生成的代理类
*/
public class StageServiceProxy extends RPCProxyBase {

    public final static class EnumCall{
        public final static int ENUM_VOID_DOSOME1_INTEGER_INTEGER = 0;
        public final static int ENUM_VOID_DOSOME2_INTEGER_INTEGER = 1;
        public final static int ENUM_STRING_DOSOME3_INTEGER = 2;
    }

    private StageServiceProxy(CallPoint callPoint){
        this.remote = callPoint;
    }
    public static StageServiceProxy inst(CallPoint callPoint) {
        return new StageServiceProxy(callPoint);
    }

    public void doSome1(Integer a, Integer b){
        Service service = Service.GetCurrent();
        service.call(remote, EnumCall.ENUM_VOID_DOSOME1_INTEGER_INTEGER, new Object[]{a, b});
    }
    public void doSome2(Integer a, Integer b){
        Service service = Service.GetCurrent();
        service.call(remote, EnumCall.ENUM_VOID_DOSOME2_INTEGER_INTEGER, new Object[]{a, b});
    }
    public String doSome3(Integer a){
        Service service = Service.GetCurrent();
        return (String)service.callWait(remote, EnumCall.ENUM_STRING_DOSOME3_INTEGER, new Object[]{a});
    }
}
