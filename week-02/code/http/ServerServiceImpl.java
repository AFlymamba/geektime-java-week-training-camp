package com.jvm.demo02;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 抽取 服务端 业务处理方法
 *
 * @author huangyin
 */
public class ServerServiceImpl {
    /**
     * 服务端服务方法
     *
     * @param socket 客户端连接
     */
    public static void service(Socket socket) {
        System.out.println("当前线程为：" + Thread.currentThread().getName());
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;character=utf-8");
            String body = "hello, nio2";
            // 这行要加，否则客户端接收读取的时候，不知道读取到什么时候结束
            printWriter.println("Content-Length:" + body.getBytes().length);
            // 打印空行
            printWriter.println();

            // 写数据
            printWriter.write(body);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != printWriter) {
                    printWriter.close();
                }
                if (null != socket) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
