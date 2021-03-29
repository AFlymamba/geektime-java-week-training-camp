package com.jvm.demo02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 基于 Socket 实现 服务器，采用手动创建线程实现多线程方式
 * <p>
 * 创建线程和销毁线程这些通用步骤耗性能
 *
 * @author huangyin
 */
public class HttpServer02 {
    public static void main(String[] agrs) {
        int port = 8082;
        ServerSocket serverSocket = null;
        try {
            // 基于 ServerSocket 创建服务端
            serverSocket = new ServerSocket(port);

            // 一直监听客户端连接
            while (true) {
                // 连接成功后，调用业务方法做相关处理
                final Socket socket = serverSocket.accept();
                new Thread(() -> {
                    ServerServiceImpl.service(socket);
                }).start();
            }
        } catch (IOException e) {
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
}
