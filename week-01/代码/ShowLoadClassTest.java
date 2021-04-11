package com.jvm.test;

import sun.misc.Launcher;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * 显示各个类加载器加载的 jar
 *
 * @author huangyin
 */
public class ShowLoadClassTest {
    public static void main(String[] args) {
        // 启动类加载器
        URL[] urLs = Launcher.getBootstrapClassPath().getURLs();
        for (URL urL : urLs) {
            System.out.println(" ==> " + urL.toExternalForm());
        }

        System.out.println("-----------------------------------");

        ClassLoader appClassLoader = ShowLoadClassTest.class.getClassLoader();
        // 扩展类加载器
        ClassLoader extClassLoader = appClassLoader.getParent();
        printClassLoader("扩展类加载器", extClassLoader);

        System.out.println("-----------------------------------");
        // 应用类加载器
        printClassLoader("应用类加载器", appClassLoader);
    }

    public static void printClassLoader(String name, ClassLoader classLoader) {
        if (null != classLoader) {
            System.out.println(name + " ClassLoader -> " + classLoader.toString());
            printURLForClassLoader(classLoader);
        } else {
            System.out.println(name + " ClassLoader -> null");
        }
    }

    private static void printURLForClassLoader(ClassLoader cl) {
        Object ucp = insightField(cl, "ucp");
        Object path = insightField(ucp, "path");
        ArrayList ps = (ArrayList) path;
        ps.forEach(obj -> {
            System.out.println(" ==> " + obj.toString());
        });
    }

    private static Object insightField(Object obj, String fName) {
        try {
            Field f;
            if (obj instanceof URLClassLoader) {
                f = URLClassLoader.class.getDeclaredField(fName);
            } else {
                f = obj.getClass().getDeclaredField(fName);
            }
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
