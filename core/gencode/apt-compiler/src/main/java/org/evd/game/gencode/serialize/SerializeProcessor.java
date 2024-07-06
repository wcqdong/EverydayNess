package org.evd.game.gencode.serialize;

import com.google.auto.service.AutoService;
import org.evd.game.annotation.SerializeClass;
import org.evd.game.annotation.SerializeField;
import org.evd.game.base.ISerializable;
import org.evd.game.gencode.AptUtils;
import org.evd.game.gencode.GenConst;
import org.evd.game.gencode.ProcessorBase;
import org.evd.game.gencode.struct.ClassStruct;
import org.evd.game.gencode.struct.FieldStruct;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.util.*;


@AutoService(Processor.class)
public class SerializeProcessor extends ProcessorBase {
    /** 文件后缀 */
    public final static String CLASS_SUFFIX = "IOSerializer";
    /** IOSerializer模板 */
    public final static String TEMPLATE_SERIALIZE_IO = "IOSerializer.ftl";
    /** REGISTER_CLASS */
    public final static String REGISTER_CLASS = "SerializerRegister";
    public final static String REGISTER_PACKAGE = "org.evd.game.";
    /** SerializerRegister模板 */
    public final static String TEMPLATE_SERIALIZE_REGISTER = "SerializerRegister.ftl";

    private List<ClassStruct> structList = new ArrayList<>();

    @Override
    protected Set<String> supportAnnotation() {
        return Collections.singleton(SerializeClass.class.getCanonicalName());
    }

    @Override
    protected void init() {

    }

    @Override
    protected void gen(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        println("");
        println("开始执行Serialize Processor");

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SerializeClass.class);
        if (elements == null || elements.isEmpty()) return;
        for (Element e : elements){
            structList.add(new ClassStruct(e, processingEnv));
        }

        for (ClassStruct clazz : structList){
            genIoSerializer(clazz);
        }

        genSerializerRegister();
    }

    private void genSerializerRegister() {

        ClassStruct struct = structList.getFirst();
        println("aaaa targetPath = " + struct.getPackageName());

        int startIndex = struct.getPackageName().indexOf(REGISTER_PACKAGE);
        int endIndex = struct.getPackageName().indexOf(".", startIndex + REGISTER_PACKAGE.length());
        if (endIndex < 0)
            endIndex = struct.getPackageName().length();
        String packageName = struct.getPackageName().substring(startIndex, endIndex);
        String packageDir = packageName.replaceAll("\\.", File.separator + File.separator);
        Map<String, Object> rootMap = getRootMap(packageName);

        println("aaaa targetPath = " + packageDir);

        String targetPath = getProjectRootPackagePath("a");
        println("aaaa targetPath = " + targetPath);

        targetPath += packageDir + File.separator;
        println("aaaa targetPath = " + targetPath);
        String javaFileName = REGISTER_CLASS + ".java";
        try {
            AptUtils.freeMarker(GenConst.TEMPLATE_DIR, TEMPLATE_SERIALIZE_REGISTER, rootMap, targetPath, javaFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getRootMap(String packageName) {
        Map<String, Object> dataModel = new HashMap<>();
        List<String> importsModel = new ArrayList<>();
        List<Map<String, Object>> fieldInfos = new ArrayList<>();
        List<Map<String, Object>> enumInfos = new ArrayList<>();

        dataModel.put("packageName", packageName);
        dataModel.put("className", "SerializerRegister");
        dataModel.put("importPackages", importsModel);
        dataModel.put("fields", fieldInfos);
        dataModel.put("enums", enumInfos);
        for (ClassStruct classStruct : structList){
            // 抽象类不能序列化
            if(classStruct.isAbstract()){
                continue;
            }
            Map<String, Object> fieldInfo = new HashMap<>();
            if (classStruct.isEnum()){
                enumInfos.add(fieldInfo);
            }else{
                fieldInfos.add(fieldInfo);
            }

            String fullClassName = classStruct.getFullClassName();
            int hashCode = fullClassName.hashCode();
            fieldInfo.put("key", String.valueOf(hashCode));
            if (classStruct.isEnum()){
            }else{
                fieldInfo.put("serializerFullName", fullClassName + CLASS_SUFFIX);
                fieldInfo.put("serializerName", classStruct.getClassName() + CLASS_SUFFIX);
            }
            fieldInfo.put("classFullName", fullClassName);
            fieldInfo.put("className", classStruct.getClassName());

        }

        return dataModel;
    }

    private void genIoSerializer(ClassStruct clazz) {
        if (clazz.isEnum()) return;

        Map<String, Object> rootMap = getRootMap(clazz);
        String packageName = clazz.getPackageName();
        String className = clazz.getClassName();
        String targetPath = getGenPath(packageName, className);
        String javaFileName = className + CLASS_SUFFIX + ".java";
        try {
            AptUtils.freeMarker(GenConst.TEMPLATE_DIR, TEMPLATE_SERIALIZE_IO, rootMap, targetPath, javaFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getRootMap(ClassStruct clazz) {
        Map<String, Object> dataModel = new HashMap<>();
        List<String> importsModel = new ArrayList<>();
        List<Map<String, Object>> fieldInfos = new ArrayList<>();

        ClassStruct superClass = clazz.getSuperClass();
        if(!superClass.getClassName().equals("Object")){
            dataModel.put("superClass", superClass.getFullClassName() + CLASS_SUFFIX);
        }
        dataModel.put("packageName", clazz.getPackageName());
        dataModel.put("className", clazz.getClassName());
        dataModel.put("proxyName", clazz.getClassName() + CLASS_SUFFIX);
        dataModel.put("importPackages", importsModel);
        dataModel.put("fields", fieldInfos);

        for(FieldStruct f : clazz.getFields(SerializeField.class)){
            // 模板所需数据
            Map<String, Object> field = new LinkedHashMap<>();
            fieldInfos.add(field);
            getTypeInfo(f, field);

        }

        return dataModel;

    }

    /**
     * 获取属性类型信息
     * @param field 字段信息
     * @return 类型信息
     */
    private void getTypeInfo(FieldStruct field, Map<String, Object> info) {
        String name = field.getName();
        String type = AptUtils.typeToBase(field.getType());
        info.put("name", name);
        info.put("type", type);
        if(field.isPrimitive() || field.isString()){
            info.put("kind", 1);
        }else if (field.isArray()){
            info.put("kind", 2);
            String elementType = type.replace("[]", "");
            info.put("elementType", elementType);
            info.put("elementIsPrimary", AptUtils.isPrimary(elementType) || AptUtils.isString(elementType) || AptUtils.isObject(elementType));
        }else if(field.isAssignableFrom(List.class)){
            info.put("kind", 3);
//            String elementType = type.replace("[]", "");
//            info.put("elementType", elementType);
//            info.put("elementIsPrimary", AptUtils.isPrimary(elementType) || AptUtils.isString(elementType) || AptUtils.isObject(elementType));
        }else if(field.isAssignableFrom(Map.class)){
            info.put("kind", 4);
        }else if(field.isAssignableFrom(Set.class)){
            info.put("kind", 5);
        }else if(field.isAssignableFrom(ISerializable.class)){
            info.put("kind", 6);
            info.put("serializeType", field.getType() + CLASS_SUFFIX);
        }else {
            info.put("kind", 7);
        }
    }

}
