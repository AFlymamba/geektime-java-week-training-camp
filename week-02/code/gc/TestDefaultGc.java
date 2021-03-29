package com.jvm.demo02;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * 打印 配置不同的 GC 策略，采用的 GC 算法
 *
 * @author huangyin
 */
public class TestDefaultGc {
    /**
     * 采用 不同 GC 策略去获取 对不同代的处理
     *
     * @param args
     */
    public static void main(String[] args) {
        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean : beans) {
            System.out.println(bean.getName());
        }
    }
}
