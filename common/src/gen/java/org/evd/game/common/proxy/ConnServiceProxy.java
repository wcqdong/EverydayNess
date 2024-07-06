package org.evd.game.common.proxy;

import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.RPCProxyBase;
import org.evd.game.runtime.Service;

/**
* 根据ConnServiceService生成的代理类
*/
public class ConnServiceProxy extends RPCProxyBase {

    public final static class EnumCall{
        public final static int ENUM_VOID_CON = 0;
        public final static int ENUM_VOID_CON1 = 1;
        public final static int ENUM_VOID_CON2 = 2;
    }

    private ConnServiceProxy(CallPoint callPoint){
        this.remote = callPoint;
    }
    public static ConnServiceProxy inst(CallPoint callPoint) {
        return new ConnServiceProxy(callPoint);
    }

    /**
    * @see org.evd.game.ConnService.ConnService#con()
    */
    public void con(){
        Service service = Service.getCurrent();
        service.call(remote, EnumCall.ENUM_VOID_CON, new Object[]{});
    }
    /**
    * @see org.evd.game.ConnService.ConnService#con1()
    */
    public void con1(){
        Service service = Service.getCurrent();
        service.call(remote, EnumCall.ENUM_VOID_CON1, new Object[]{});
    }
    /**
    * @see org.evd.game.ConnService.ConnService#con2()
    */
    public void con2(){
        Service service = Service.getCurrent();
        service.call(remote, EnumCall.ENUM_VOID_CON2, new Object[]{});
    }
}
