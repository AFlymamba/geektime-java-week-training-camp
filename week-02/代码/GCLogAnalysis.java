package com.jvm.demo02;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 演示 GC 日志生成与解读
 *
 * @author huangyin
 */
public class GCLogAnalysis {
    private static Random random = new Random();

    public static void main(String[] args) {
        long startMillis = System.currentTimeMillis();
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        long endMills = startMillis + timeoutMillis;

        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分，进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];

        while (System.currentTimeMillis() < endMills) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100 * 1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束，共生成对象次数：" + counter.longValue());
    }

    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                String randomStr = "randomStr-Anything";
                while (stringBuilder.length() < randomSize) {
                    stringBuilder.append(randomStr);
                    stringBuilder.append(max);
                    stringBuilder.append(randomSize);
                }
                result = stringBuilder.toString();
                break;
        }
        return result;
    }
}
