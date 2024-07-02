package ${commonPackageName};

import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.RPCProxyBase;
import org.evd.game.runtime.Service;
<#if singleton>
import org.evd.game.runtime.DistributeConfig;
</#if>
<#if importPackages??>
    <#list importPackages as package>
        import ${package};
    </#list>
</#if>

/**
* 根据${className}Service生成的代理类
*/
public class ${className}Proxy extends RPCProxyBase {

    public final static class EnumCall{
    <#list methods as method>
        public final static int ${method.enumCall} = ${method.methodKey};
    </#list>
    }

    <#if singleton>
    private static final String SERV_NAME = "${serviceName}";
    private static CallPoint callPoint;

    public static ${className}Proxy inst() {
        ${className}Proxy proxy = new ${className}Proxy();
        if(callPoint == null){
            callPoint = DistributeConfig.getNode(SERV_NAME);
        }
        proxy.remote = callPoint;
        return proxy;
    }
    <#else>
    private ${className}Proxy(CallPoint callPoint){
        this.remote = callPoint;
    }
    public static ${className}Proxy inst(CallPoint callPoint) {
        return new ${className}Proxy(callPoint);
    }
    </#if>

    <#list methods as method>
    public ${method.returnType} ${method.methodName}(${method.formalParams}){
        Service service = Service.getCurrent();
        <#if method.returnType == "void">
        service.call(remote, EnumCall.${method.enumCall}, new Object[]{${method.nameParams}});
        <#else >
        return (${method.returnType})service.callWait(remote, EnumCall.${method.enumCall}, new Object[]{${method.nameParams}});
        </#if>
    }
    </#list>
}
