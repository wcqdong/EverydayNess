package org.evd.gencode;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {
    public static void freeMarker(String pathPrefix, String tempFile, Object rootMap,
                                  String targetDir, String targetFile) throws Exception {
        Configuration configuration = new Configuration();
        File path = new File(pathPrefix);
        configuration.setDirectoryForTemplateLoading(path);
//        configuration.setClassForTemplateLoading(Utils.class, pathPrefix);
        configuration.setEncoding(Locale.getDefault(), "UTF-8");
        Template temp = configuration.getTemplate(tempFile, "UTF-8");

        // 判断目标文件夹不存在 ，则新建文件夹
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 目标文件名(包含路径的名称)
        String fileFullName = targetDir + targetFile;
        System.out.println("---------开始生成" + fileFullName + "文件......---------");

        // 根据模版生成文件
        File target = new File(fileFullName);
        Writer out = new OutputStreamWriter(Files.newOutputStream(target.toPath()), "UTF-8");
        temp.process(rootMap, out);
        out.flush();
        out.close();

        System.out.println("---------" + targetFile + "文件生成完毕！---------\n");
    }

    public static class StringExt{
        public StringBuilder sbf;

        public StringExt(){
            this.sbf = new StringBuilder();
        }
        public StringExt(String str){
            this.sbf = new StringBuilder(str);
        }

        public StringExt appendJoin(String append, String split){
            sbf.append(append).append(split);
            return this;
        }

        public StringExt appendJoin(String append){
            sbf.append(append).append(File.separator);
            return this;
        }
        public StringExt append(String append){
            sbf.append(append);
            return this;
        }

        @Override
        public String toString() {
            return sbf.toString();
        }
    }


    private final static Map<String, String> base2Wrapper = new HashMap<String, String>(){{
        put(int.class.getSimpleName(), Integer.class.getSimpleName());
        put(short.class.getSimpleName(), Short.class.getSimpleName());
        put(long.class.getSimpleName(), Long.class.getSimpleName());
        put(boolean.class.getSimpleName(), Boolean.class.getSimpleName());
        put(byte.class.getSimpleName(), Byte.class.getSimpleName());
        put(char.class.getSimpleName(), Character.class.getSimpleName());
        put(float.class.getSimpleName(), Float.class.getSimpleName());
        put(double.class.getSimpleName(), Double.class.getSimpleName());
    }};

    private final static Map<String, String> wrapper2Base = new HashMap<String, String>(){{
        put(int.class.getSimpleName(), Integer.class.getSimpleName());
        put(short.class.getSimpleName(), Short.class.getSimpleName());
        put(long.class.getSimpleName(), Long.class.getSimpleName());
        put(boolean.class.getSimpleName(), Boolean.class.getSimpleName());
        put(byte.class.getSimpleName(), Byte.class.getSimpleName());
        put(char.class.getSimpleName(), Character.class.getSimpleName());
        put(float.class.getSimpleName(), Float.class.getSimpleName());
        put(double.class.getSimpleName(), Double.class.getSimpleName());
    }};

    public static String typeToBase(String type){
        String base = wrapper2Base.get(type);
        return base == null ? type : base;
    }

    public static String typeToWrapper(String type){
        String wrapper = base2Wrapper.get(type);
        return wrapper == null ? type : wrapper;
    }
}
