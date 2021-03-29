package com.jvm.demo02;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 基于 Socket 实现服务端：目前是基于单线程
 * <p>
 * 套环：Socket 是 基于 ServerSocket 实现的
 * 引申：
 * JDBC 的 PreparedStatement 也是基于 JDBC 的 Connection 实现的
 *
 * @author huangyin
 */
public class HttpServer01 {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            // 1、创建 ServerSocket，绑定8081端口
            int port = 8081;
            serverSocket = new ServerSocket(port);
            Socket socket = null;

            while (true) {
                try {
                    // 2、监听客户端连接，一个连接就是一个 Socket
                    socket = serverSocket.accept();
                    // 3、业务代码
                    service(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭相关资源
                    try {
                        if (null != socket) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != serverSocket) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void service(Socket socket) {
        PrintWriter printWriter = null;
        try {
            // 基于 Socket 的 输出流，在外层套一层 打印流
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;character=utf-8");
            String body = "hello, nio1";
            // 这行要加，否则客户端接收读取的时候，不知道读取到什么时候结束
            printWriter.println("Content-Length:" + body.getBytes().length);
            // 打印空行
            printWriter.println();

            // 写数据
            printWriter.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭相关资源
            if (null != printWriter) {
                printWriter.close();
            }
        }
    }
}
