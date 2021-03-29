package com.jvm.demo02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于 ServerSocket 实现 服务端：池化思想，基于线程池去做
 *
 * @author huangyin
 */
public class HttpServer03 {
    public static void main(String[] args) {
        // 1、创建服务端
        int port = 8083;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);

            // 创建固定大小的线程池
            int threadNumber = Runtime.getRuntime().availableProcessors() + 2;
            System.out.println("线程数量为：" + threadNumber);

            ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
            while (true) {
                // 2、监听客户端连接
                final Socket socket = serverSocket.accept();
                // 3、提交任务到线程池，让线程池中的线程执行任务
                executorService.execute(() -> {ServerServiceImpl.service(socket);});
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
}
