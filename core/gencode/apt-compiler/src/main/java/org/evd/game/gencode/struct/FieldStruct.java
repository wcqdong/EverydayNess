package org.evd.game.gencode.struct;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class FieldStruct {
    private VariableElement element;
    private final ProcessingEnvironment env;
    private final Elements elementUtils;
    private final Types typeUtils;

    public FieldStruct(Element element, ProcessingEnvironment env) {
        this.element = (VariableElement)element;
        this.env = env;
        this.elementUtils = env.getElementUtils();
        this.typeUtils = env.getTypeUtils();
    }

    public String getPackageName(){
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }

    public String getType(){
        String type = element.asType().toString();
        type = type.replaceAll("java.lang.", "");
        return type;
    }

    public String getName(){
        return element.getSimpleName().toString();
    }

    public boolean isArray(){
        return element.asType().getKind() == TypeKind.ARRAY;
    }

    public boolean isPrimitive(){
        return element.asType().getKind().isPrimitive();
    }

    public boolean isString(){
        return element.asType().toString().equals("java.lang.String");
    }

    public boolean isAssignableFrom(Class<?> clazz) {
        return isAssignableFrom(element.asType(), clazz);
    }

    public VariableElement getElement() {
        return element;
    }

    /**
         * 是否是某个类的子类（包括本身）
         * @param type 类型
         * @param clazz 基类
         * @return 是 true
         */
    private boolean isAssignableFrom(TypeMirror type, Class<?> clazz) {
        // 先擦除泛型
        TypeMirror erasureType = typeUtils.erasure(type);
        if (erasureType.toString().equals(clazz.getTypeName())) {
            return true;
        }
        // 递归查找父类
        for (TypeMirror superType : typeUtils.directSupertypes(type)) {
            if (isAssignableFrom(superType, clazz)) {
                return true;
            }
        }
        return false;
    }

}
