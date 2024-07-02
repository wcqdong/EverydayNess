package org.evd.game.common.proxy;

import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.RPCProxyBase;
import org.evd.game.runtime.Service;

/**
* 根据CommonServiceService生成的代理类
*/
public class CommonServiceProxy extends RPCProxyBase {

    public final static class EnumCall{
        public final static int ENUM_VOID_COMMONTEST = 0;
    }

    private CommonServiceProxy(CallPoint callPoint){
        this.remote = callPoint;
    }
    public static CommonServiceProxy inst(CallPoint callPoint) {
        return new CommonServiceProxy(callPoint);
    }

    public void commonTest(){
        Service service = Service.getCurrent();
        service.call(remote, EnumCall.ENUM_VOID_COMMONTEST, new Object[]{});
    }
}
