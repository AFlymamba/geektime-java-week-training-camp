package com.jvm.demo01;

/**
 * 简单字节码文件
 *
 * @author huangyin
 */
public class HelloByteCode {
    private Long id;

    private String username;

    public static void main(String[] args) {
        // 创建一个 HelloByteCode 类型的实例
        // 创建对象赋给了局部变量 com.jvm.demo01.HelloByteCode
        HelloByteCode helloByteCode = new HelloByteCode();
    }
}
