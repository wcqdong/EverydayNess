package org.evd.gencode;

import com.google.auto.service.AutoService;
import org.evd.annotation.Rpc;
import org.evd.annotation.ServiceClass;
import org.evd.gencode.struct.ClassStruct;
import org.evd.gencode.struct.StructFactory;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class ServiceProcessor extends ProcessorBase {

    private List<ClassStruct<ServiceClass>> structList = new ArrayList<>();

    @Override
    protected Set<String> supportAnnotation() {
        return Collections.singleton(Rpc.class.getCanonicalName());
    }

    @Override
    protected void init() {

    }

    @Override
    protected void gen(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "");
        messager.printMessage(Diagnostic.Kind.NOTE, "开始执行Service Processor");

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ServiceClass.class);
        if (elements == null || elements.isEmpty()) return;

        structList = StructFactory.convertClass(elementUtils, elements, ServiceClass.class);

    }
}
