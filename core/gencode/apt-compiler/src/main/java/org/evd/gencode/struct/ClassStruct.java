package org.evd.gencode.struct;

import javax.lang.model.element.Element;

public class ClassStruct<T> {
    private Element element;
    private Class<T> annotationClass;
    /** 包路径 */
    public String packageName;
    /** 节点所在类名称 */
    public String className;
    public String simpleName;

    public ClassStruct(Element element, Class<T> annotationClass, String packageName, String className, String simpleName) {
        this.element = element;
        this.annotationClass = annotationClass;
        this.packageName = packageName;
        this.className = className;
        this.simpleName = simpleName;
    }
}
