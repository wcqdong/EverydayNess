package org.evd.gencode.struct;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.REUtil;
import org.evd.gencode.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class MethodStruct<T> {
    private Element element;
    private Class<T> annotationClass;
    /** 包路径 */
    public String packageName;
    public String classFullName;
    public String className;
    public String methodName;

    public String returnType;
    public String returnTypeWrapper;
    public ParamStruct[] params;


    public MethodStruct(Element element,
                        Class<T> annotationClass,
                        String packageName,
                        String classFullName,
                        String className,
                        String methodName,
                        String returnType,
                        ParamStruct[] params)
    {
        this.element = element;
        this.annotationClass = annotationClass;
        this.packageName = packageName;
        this.classFullName = classFullName;
        this.className = className;
        this.methodName = methodName;
        this.returnType = Utils.typeToBase(returnType);
        this.returnTypeWrapper = Utils.typeToWrapper(returnType);
        this.params = params;
    }

    public String toParamNames(){
        StringBuilder nameParams = new StringBuilder();
        for (int i=0; i<params.length; ++i) {
            ParamStruct paramStruct = params[i];
            nameParams.append(paramStruct.paramName);
            if (i < params.length - 1){
                nameParams.append(", ");
            }
        }
        return nameParams.toString();
    }

    public String toParamTypesWitchReturn(){
        StringBuilder typeParams = new StringBuilder();
        if (returnType.equals("void")){
            if (params.length > 0){
                typeParams.append("<");
            }
            for (int i=0; i<params.length; ++i){
                ParamStruct paramStruct = params[i];
                typeParams.append(paramStruct.paramTypeWrapper);
                if (i < params.length - 1){
                    typeParams.append(", ");
                }
            }
            if (params.length > 0){
                typeParams.append(">");
            }
        }else{
            typeParams.append("<");
            typeParams.append(returnTypeWrapper);
            for (int i=0; i<params.length; ++i){
                ParamStruct paramStruct = params[i];
                typeParams.append(", ");
                typeParams.append(paramStruct.paramTypeWrapper);
            }
            typeParams.append(">");
        }
        return typeParams.toString();
    }

    public String toParamTypeAndTypes(){
        StringBuilder formalParams = new StringBuilder();
        for (int i=0; i<params.length; ++i) {
            ParamStruct paramStruct = params[i];
            formalParams.append(paramStruct.paramType).append(" ").append(paramStruct.paramName);
            if (i < params.length - 1){
                formalParams.append(", ");
            }
        }
        return formalParams.toString();
    }


    @Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("package ").append(packageName).append("\n");
        sbf.append("\tclass ").append(classFullName).append("{").append("\n");
        sbf.append("\t\t").append(returnType).append(" ").append(methodName).append("(");
        for (int i = 0; i < params.length; i++) {
            sbf.append(params[i].toString());
            if (i < params.length - 1) {
                sbf.append(", ");
            }
        }
        sbf.append(")").append("\n");
        sbf.append("\t}");
        return sbf.toString();
    }

    public TypeElement getTypeElement() {
        return (TypeElement)element.getEnclosingElement();
    }
}
