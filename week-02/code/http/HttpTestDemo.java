package com.http.demo;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * 基于 Apache HttpClient 实现 Http 请求
 *
 * @author huangyin
 */
public class HttpTestDemo {
    /**
     * 测试 单线程 server
     */
    @Test
    public void testSingleThreadHttpServer() {
        String requestUrl = "http://localhost:8081";
        doGet(requestUrl);
    }

    /**
     * 使用 Apache 的 HttpClient 库去完成 Http 请求
     *
     * @param requestUrl 请求地址
     */
    public static void doGet(final String requestUrl) {
        // 创建 Http 客户端构造器对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();

        // 创建一个 HttpGet 对象，负责封装 Get 请求需要的数据
        HttpGet httpGet = new HttpGet(requestUrl);
        CloseableHttpResponse response = null;
        try {
            // 发起 Http 请求
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            System.out.println(entity);

            String content = EntityUtils.toString(entity);
            System.out.println("content：" + content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close resources
            try {
                if (null != response) {
                    response.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
