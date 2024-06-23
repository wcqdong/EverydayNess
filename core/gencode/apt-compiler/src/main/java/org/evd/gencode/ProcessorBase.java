package org.evd.gencode;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.Set;

public abstract class ProcessorBase extends AbstractProcessor {
    /** Element操作类 */
    protected Elements elementUtils;
    /** 类信息工具类 */
    protected Types typeUtils;
    /** 日志工具类 */
    protected Messager messager;
    /** 文件创建工具类 */
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        init();
    }

    /**
     * 指明有哪些注解需要被扫描到，返回注解的全路径（包名+类名）
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportAnnotation();
    }

    protected abstract Set<String> supportAnnotation() ;

    /**
     * 用来指定当前正在使用的Java版本，一般返回SourceVersion.latestSupported()表示最新的java版本即可
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) return false;
        gen(annotations, roundEnv);
        return false;
    }

    protected void println(String str){
        messager.printMessage(Diagnostic.Kind.NOTE, str);
    }

    protected abstract void init();
    protected abstract void gen(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
