package org.evd.gencode.rpc;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.tree.JCTree;
import org.evd.annotation.Rpc;
import org.evd.annotation.ServiceClass;
import org.evd.gencode.GenConst;
import org.evd.gencode.ProcessorBase;
import org.evd.gencode.Utils;
import org.evd.gencode.struct.MethodStruct;
import org.evd.gencode.struct.ParamStruct;
import org.evd.gencode.struct.StructFactory;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.util.*;

import static org.evd.gencode.GenConst.ROOT_PROJECT_PATH;


@AutoService(Processor.class)
public class RpcProcessor extends ProcessorBase {
    private final String pathService = ROOT_PROJECT_PATH + "Services" + File.separator;

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

        // debug
//        for (MethodStruct<Rpc> method : structList) {
//            println(method.toString());
//        }

        group();

        classMap.forEach((classFullName, methods)->{
            Map<String, Object> rootMap = getRootMap(methods);
            genRpcImp(rootMap, classFullName);
            genRpcProxy(rootMap, classFullName);
        });

    }

    private Map<String, Object> getRootMap(List<MethodStruct<Rpc>> methods) {
        MethodStruct<Rpc> struct = methods.get(0);

        Map<String, Object> dataModel = new HashMap<>();
        List<String> importsModel = new ArrayList<>();
        List<Map<String, Object>> methodsModel = new ArrayList<>();

        dataModel.put("packageName", struct.packageName);
        dataModel.put("commonPackageName", "org.evd.common.proxy");
        dataModel.put("className", struct.className);
        dataModel.put("importPackages", importsModel);
        dataModel.put("methods", methodsModel);
        TypeElement classElement = struct.getTypeElement();
        ServiceClass serviceAnnotation = classElement.getAnnotation(ServiceClass.class);
        if (serviceAnnotation == null){
            println(struct.className + "don't have @ServiceClass annotation");
            return dataModel;
        }
        dataModel.put("singleton", serviceAnnotation.singleton());

        for (int i=0; i<methods.size(); ++i) {
            // enumCall-----------------
            MethodStruct<Rpc> method = methods.get(i);
            Map<String, Object> methodModel = new HashMap<>();
            methodsModel.add(methodModel);

            Utils.StringExt enumCall = new Utils.StringExt()
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
        String[] array = classFullName.split("\\.");
        // 包的第三级为工程名
        String projectName = array[2];
        String targetPath = pathService + projectName + File.separator
                + "src" + File.separator
                + "gen" + File.separator
                + "java" + File.separator
                + packageName.replaceAll("\\.", File.separator + File.separator) + File.separator;
        String javaFileName = className + "Impl.java";

//        println("templateFolder: " + templateFolder);
//        println("TEMPLATE_RPC_IMP: " + TEMPLATE_RPC_IMP);
//        println("genFilePath: " + genFilePath);
//        println("javaFileName: " + javaFileName);

        try {
            Utils.freeMarker(GenConst.TEMPLATE_DIR, TEMPLATE_RPC_IMP, rootMap, targetPath, javaFileName);
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
        Utils.StringExt targetPath = new Utils.StringExt(ROOT_PROJECT_PATH);
        targetPath.appendJoin("common");
        targetPath.appendJoin("src");
        targetPath.appendJoin("gen");
        targetPath.appendJoin("java");
        targetPath.appendJoin(array[0]);
        targetPath.appendJoin(array[1]);
        targetPath.appendJoin("common");
        targetPath.appendJoin("proxy");

        try {
            Utils.freeMarker(GenConst.TEMPLATE_DIR, TEMPLATE_RPC_PROXY, rootMap, targetPath.toString(), javaFileName);
            println("generate success [" + javaFileName + "]");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void group() {
        for (MethodStruct<Rpc> method : structList) {
            List<MethodStruct<Rpc>> methods = classMap.computeIfAbsent(method.classFullName, k -> new ArrayList<>());
            methods.add(method);
        }
    }
}
