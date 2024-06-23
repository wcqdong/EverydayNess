package org.evd.gencode;

import java.io.File;


public class GenConst{
    public static String ROOT_PROJECT_PATH = System.getProperty("user.dir") + File.separator;
    static {
        // TODO gradle task的build和从idea启动编译，ROOT_PROJECT_PATH的路径不一样
        if (!ROOT_PROJECT_PATH.endsWith("EverydayNess")){
            ROOT_PROJECT_PATH = ROOT_PROJECT_PATH.substring(0, ROOT_PROJECT_PATH.indexOf("EverydayNess") + "EverydayNess".length() + 1);
        }
    }
    public static final String TEMPLATE_DIR = ROOT_PROJECT_PATH +  "core/gencode/apt-compiler/src/main/resources/templates/";
}
