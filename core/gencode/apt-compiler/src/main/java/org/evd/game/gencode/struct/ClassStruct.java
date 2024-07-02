package org.evd.game.gencode.struct;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ClassStruct {
    private final TypeElement element;
    private final ProcessingEnvironment env;
    private final Elements elementUtils;
    private final Types typeUtils;

    public ClassStruct(Element element, ProcessingEnvironment env) {
        this.element = (TypeElement) element;
        this.env = env;
        this.elementUtils = env.getElementUtils();
        this.typeUtils = env.getTypeUtils();
    }

    public List<FieldStruct> getFields(){
        List<FieldStruct> fieldElements = new ArrayList<>();
        for(Element e : element.getEnclosedElements()){
            if (e instanceof VariableElement){
                fieldElements.add(new FieldStruct(e, env));
            }
        }
        return fieldElements;
    }

    public <A extends Annotation> List<FieldStruct> getFields(Class<A> clazz){
        List<FieldStruct> fieldElements = new ArrayList<>();
        for(Element e : element.getEnclosedElements()){
            if (e instanceof VariableElement && e.getAnnotation(clazz) != null){
                fieldElements.add(new FieldStruct(e, env));
            }
        }
        return fieldElements;
    }
    public String getPackageName(){
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }

    public String getFullClassName(){
        return element.asType().toString();
    }

    public String getClassName(){
        return element.getSimpleName().toString();
    }

    public ClassStruct getSuperClass(){
        Element e = typeUtils.asElement(element.getSuperclass());
        return new ClassStruct(e, env);
    }

    public boolean isAbstract() {
        return element.getModifiers().contains(Modifier.ABSTRACT);
    }

    public TypeElement getElement() {
        return element;
    }
    public boolean isEnum(){
        return element.getKind() == ElementKind.ENUM;
    }
}
