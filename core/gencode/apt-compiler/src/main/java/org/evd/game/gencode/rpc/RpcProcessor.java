package org.evd.game.gencode.rpc;

import com.google.auto.service.AutoService;
import org.evd.game.annotation.Rpc;
import org.evd.game.annotation.Actor;
import org.evd.game.gencode.GenConst;
import org.evd.game.gencode.struct.MethodStruct;
import org.evd.game.gencode.ProcessorBase;
import org.evd.game.gencode.AptUtils;
import org.evd.game.gencode.struct.ParamStruct;
import org.evd.game.gencode.struct.StructFactory;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;


@AutoService(Processor.class)
public class RpcProcessor extends ProcessorBase {

    private final static String TEMPLATE_RPC_IMP = "RpcImp.ftl";
    private final static String TEMPLATE_RPC_PROXY = "RpcProxy.ftl";

    private List<MethodStruct<Rpc>> structList = new ArrayList<>();
    private Map<String, List<MethodStruct<Rpc>>> classMap = new HashMap<>();

    @Override
    protected Set<String> supportAnnotation() {
        return Collections.singleton(Rpc.class.getCanonicalName());
    }

    @Override
    protected void init() {

    }

    @Override
    protected void gen(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        println("");
        println("开始执行Rpc Processor");

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Rpc.class);
        if (elements == null || elements.isEmpty()) return;
        structList = StructFactory.convertMethod(elementUtils, elements, Rpc.class);


        MethodStruct<Rpc> struct = structList.getFirst();
        String targetPath = getGenPath(struct.packageName, struct.className);


        // debug
        for (MethodStruct<Rpc> method : structList) {
            println(method.toString());
        }

        group();

        classMap.forEach((classFullName, methods)->{
            Map<String, Object> rootMap = getRootMap(methods);
            genRpcImp(rootMap, classFullName);
            genRpcProxy(rootMap, classFullName);
        });

    }

    private Map<String, Object> getRootMap(List<MethodStruct<Rpc>> methods) {
        MethodStruct<Rpc> struct = methods.getFirst();

        Map<String, Object> dataModel = new HashMap<>();
        List<String> importsModel = new ArrayList<>();
        List<Map<String, Object>> methodsModel = new ArrayList<>();

        dataModel.put("packageName", struct.packageName);
        dataModel.put("commonPackageName", "org.evd.game.common.proxy");
        dataModel.put("className", struct.className);
        dataModel.put("fullClassName", struct.fullClassName);
        dataModel.put("importPackages", importsModel);
        dataModel.put("methods", methodsModel);
        TypeElement classElement = struct.getTypeElement();
        Actor serviceAnnotation = classElement.getAnnotation(Actor.class);
        if (serviceAnnotation == null){
            println(struct.className + "don't have @ServiceClass annotation");
            return dataModel;
        }
        dataModel.put("singleton", serviceAnnotation.single());

        for (int i=0; i<methods.size(); ++i) {
            // enumCall-----------------
            MethodStruct<Rpc> method = methods.get(i);
            Map<String, Object> methodModel = new HashMap<>();
            methodsModel.add(methodModel);

            AptUtils.StringExt enumCall = new AptUtils.StringExt()
                    .appendJoin("ENUM", "_")
                    .appendJoin(method.returnType.toUpperCase(), "_")
                    .append(method.methodName.toUpperCase());
            for (ParamStruct paramStruct : method.params){
                enumCall.append("_");
                enumCall.append(paramStruct.paramType.toUpperCase());
            }

            methodModel.put("enumCall", enumCall.toString());
            // enumCall-----------------

            methodModel.put("methodKey", i);

            methodModel.put("paramSize", method.params.length);
            methodModel.put("methodName", method.methodName);

            // func paramTypes---------------
            String func;
            if (method.returnType.equals("void")){
                func = "Function";
            }else{
                func = "ReturnFunction";
            }
            methodModel.put("func", func);
            methodModel.put("typeParams", method.toParamTypesWitchReturn());
            // func paramTypes---------------

            methodModel.put("returnType", method.returnType);

            methodModel.put("formalParams", method.toParamTypeAndTypes());
            methodModel.put("nameParams", method.toParamNames());
        }
        return dataModel;

    }

    private void genRpcImp(Map<String, Object> rootMap , String classFullName) {
        println("classFullName: " + classFullName);

        int splitIndex = classFullName.lastIndexOf(".");
        String packageName = classFullName.substring(0, splitIndex);
        println("packageName: " + packageName);
        String className = classFullName.substring(splitIndex + 1);
        println("className: " + className);
        // 包的第三级为工程名
        String targetPath = getGenPath(packageName, className);
        String javaFileName = className + "Impl.java";

        try {
            AptUtils.freeMarker(GenConst.TEMPLATE_DIR, TEMPLATE_RPC_IMP, rootMap, targetPath, javaFileName);
            println("generate success [" + javaFileName + "]");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void genRpcProxy(Map<String, Object> rootMap, String classFullName) {
        int splitIndex = classFullName.lastIndexOf(".");
        String className = classFullName.substring(splitIndex + 1);
        String javaFileName = className + "Proxy.java";

        String[] array = classFullName.split("\\.");
        AptUtils.StringExt targetPath = new AptUtils.StringExt(GenConst.ROOT_PROJECT_PATH);
        targetPath.appendJoin("common");
        targetPath.appendJoin("src");
        targetPath.appendJoin("gen");
        targetPath.appendJoin("java");
        targetPath.appendJoin(array[0]);
        targetPath.appendJoin(array[1]);
        targetPath.appendJoin(array[2]);
        targetPath.appendJoin("common");
        targetPath.appendJoin("proxy");

        try {
            AptUtils.freeMarker(GenConst.TEMPLATE_DIR, TEMPLATE_RPC_PROXY, rootMap, targetPath.toString(), javaFileName);
            println("generate success [" + javaFileName + "]");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void group() {
        for (MethodStruct<Rpc> method : structList) {
            List<MethodStruct<Rpc>> methods = classMap.computeIfAbsent(method.fullClassName, k -> new ArrayList<>());
            methods.add(method);
        }
    }
}
