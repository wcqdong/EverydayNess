package org.evd.common.proxy;

import org.evd.runtime.CallPoint;
import org.evd.runtime.RPCProxyBase;
import org.evd.runtime.Service;

/**
* 根据ConnServiceService生成的代理类
*/
public class ConnServiceProxy extends RPCProxyBase {

    public final static class EnumCall{
        public final static int ENUM_VOID_CON = 0;
        public final static int ENUM_VOID_CON1 = 1;
    }

    private ConnServiceProxy(CallPoint callPoint){
        this.remote = callPoint;
    }
    public static ConnServiceProxy inst(CallPoint callPoint) {
        return new ConnServiceProxy(callPoint);
    }

    public void con(){
        Service service = Service.GetCurrent();
        service.call(remote, EnumCall.ENUM_VOID_CON, new Object[]{});
    }
    public void con1(){
        Service service = Service.GetCurrent();
        service.call(remote, EnumCall.ENUM_VOID_CON1, new Object[]{});
    }
}
