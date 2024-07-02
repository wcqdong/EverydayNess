package ${packageName};

import org.evd.game.runtime.RPCImplBase;
import org.evd.game.runtime.Service;
import org.evd.game.runtime.support.function.*;
<#if importPackages??>
    <#list importPackages as package>
        import ${package};
    </#list>
</#if>

/**
* 根据${className}Service生成的rpc分发类
*/
public class ${className}Impl extends RPCImplBase {
    public final static class EnumCall{
    <#list methods as method>
        public final static int ${method.enumCall} = ${method.methodKey};
    </#list>
    }

    @Override
    public Object getMethodFunction(Service serv, int methodKey) {
        ${className} service = (${className}) serv;
        switch (methodKey){
            <#list methods as method>
            case EnumCall.${method.enumCall}:
                return (${method.func}${method.paramSize}${method.typeParams})service::${method.methodName};
            </#list>
            default:
                return null;
        }
    }
}
