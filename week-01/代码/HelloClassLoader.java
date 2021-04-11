package com.jvm.demo01;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Base64;

/**
 * 自定义类加载器：
 *  ①继承 ClassLoader
 *  ②重写 findClass方法
 *
 * @author huangyin
 */
public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) {
        try {
            // 父类直接抛出了 ClassNotFountException 异常
            new HelloClassLoader().findClass("E:\\workspaces\\workspace-practice\\daily_code_record\\jvm-demo\\src\\com\\jvm\\demo01\\Hello").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        System.out.println("重写的findClass方法");
//        // base64 编码
//        String helloBase64 = "dGhpcyBpcyBhIGNsYXNzTG9hZGVyLHRvIGRlYWwgSGVsbG8gY2xhc3M=";
//        // 解码
//        byte[] decode = decode(helloBase64);
//        return defineClass(name, decode, 0, decode.length);
        try {
            byte[] data = loadByte(name);
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }

    private byte[] loadByte(String name) throws Exception {
        String filePath = name.replaceAll("\\.", "/");
        FileInputStream fis = new FileInputStream(filePath + ".class");
        int len = fis.available();
        byte[] data = new byte[len];
        fis.read(data);
        fis.close();

        // 解码等业务操作
        return data;
    }

    private byte[] decode(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}
