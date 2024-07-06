package org.evd.game.common;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 *
 * 通过给定的包名，来得到包下的全部CLASS类
 */
public class ClassFinder {

    /**
     * 将从配置文件中读取的形式
     * @param packageName 包名
     * @return
     */
    public static String packageToPath(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }

    /**
     * 读取包内所有的类获取class对象，并根据指定的条件过滤
     *
     * @param packageName 包名
     * @return 反射类
     */
    public static List<Class<?>> getAllClass(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String packageDirName = packageName.replace('.', '/');

        try {
            Enumeration<URL> dirs = cl.getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();

                String protocol = url.getProtocol();

                if ("file".equals(protocol))
                    findByFile(cl, packageName, URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8), classes);
                else if ("jar".equals(protocol))
                    findInJar(cl, packageName, packageDirName, url, classes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Class<?>> result = new ArrayList<>(classes);
        result.sort(Comparator.comparing(Class::getName));
        return result;
    }

    /**
     * 从文件获取java类
     *
     * @param cl 类加载器
     * @param packageName 包名
     * @param filePath 类文件路径
     * @param classes 扫描到的类
     */
    private static void findByFile(ClassLoader cl, String packageName, String filePath, Set<Class<?>> classes) throws IOException {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory())
            return;

        int length = filePath.length();
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.toString();
                if(!fileName.endsWith(".class")){
                    return FileVisitResult.CONTINUE;
                }
                String className = packageName + "." + file.toString().substring(length, fileName.length() - 6).replace(File.separator, ".");
                try {
                    Class<?> clazz = cl.loadClass(className);
                    classes.add(clazz);
                    return FileVisitResult.CONTINUE;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    /**
     * 读取jar中的java类
     *
     * @param cl 类加载器
     * @param pname 包名
     * @param packageDirName 包的路径
     * @param url
     * @param classes 扫描到的类
     */
    public static void findInJar(ClassLoader cl, String pname, String packageDirName, URL url, Set<Class<?>> classes) {
        try {
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.isDirectory())
                    continue;

                String name = entry.getName();

                if (name.charAt(0) == '/')
                    name = name.substring(1);

                if (name.startsWith(packageDirName) && name.contains("/") && name.endsWith(".class")) {
                    name = name.substring(0, name.length() - 6).replace('/', '.');
                    try {
                        Class<?> clazz = cl.loadClass(name);
                        classes.add(clazz);
                    } catch (Throwable e) {
                        System.out.println("无法直接加载的类：" + name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


