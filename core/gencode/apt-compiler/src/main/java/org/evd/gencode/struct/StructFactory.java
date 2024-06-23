package org.evd.gencode.struct;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructFactory {

    public static <T> List<ClassStruct<T>> convertClass(Elements mElementUtils, Set<? extends Element> elements, Class<T> annotationClass) {
        List<ClassStruct<T>> result = new ArrayList<>();
        // 遍历节点
        for (Element element : elements) {
            // 获取节点包信息
            String packageName = mElementUtils.getPackageOf(element).getQualifiedName().toString();
            // 获取节点类信息，由于 @BindView 作用于成员属性上，所以这里使用 getEnclosingElement() 获取父节点信息
//            String className = element.getEnclosingElement().getSimpleName().toString();
            // 获取节点类型
            String className = element.asType().toString();
            // 获取节点标记的属性名称
            String simpleName = element.getSimpleName().toString();

            ClassStruct<T> cs = new ClassStruct<>(element, annotationClass, packageName, className, simpleName);

            result.add(cs);
        }
        return result;
    }

    public static <T> List<MethodStruct<T>> convertMethod(Elements mElementUtils, Set<? extends Element> elements, Class<T> annotationClass) {
        List<MethodStruct<T>> result = new ArrayList<>();
        // 遍历节点
        for (Element e : elements) {
            ExecutableElement element = (ExecutableElement)e;
            // 获取节点包信息
            String packageName = mElementUtils.getPackageOf(element).getQualifiedName().toString();
            // 获取节点类信息，由于 @BindView 作用于成员属性上，所以这里使用 getEnclosingElement() 获取父节点信息
            String classFullName = ((TypeElement)(element.getEnclosingElement())).getQualifiedName().toString();
            String className = element.getEnclosingElement().getSimpleName().toString();
            String returnType = element.getReturnType().toString();
            returnType = returnType.replace("java.lang.", "");
            List<? extends VariableElement> paramList = element.getParameters();
            ParamStruct[] params = new ParamStruct[paramList.size()];
            for (int i = 0; i < paramList.size(); i++) {
                String typeName = paramList.get(i).asType().toString().replace("java.lang.", "");
                String paramName = paramList.get(i).getSimpleName().toString();
                params[i] = new ParamStruct(typeName, paramName);
            }

            // 获取节点标记的属性名称
            String methodName = element.getSimpleName().toString();

            MethodStruct<T> cs = new MethodStruct<>(element, annotationClass, packageName, classFullName, className, methodName, returnType, params);

            result.add(cs);
        }
        return result;
    }
}
