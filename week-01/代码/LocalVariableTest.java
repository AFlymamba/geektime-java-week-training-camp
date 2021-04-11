package com.jvm.demo01;

/**
 * 局部变量数组测试
 *
 * @author huangyin
 */
public class LocalVariableTest {
    public static void main(String[] args) {
        MovingAverage movingAverage = new MovingAverage();

        // 累加
        int sum1 = 1;
        int sum2 = 2;
        movingAverage.submit(sum1);
        movingAverage.submit(sum2);

        // 获取平均数
        double avg = movingAverage.getAvg();
        System.out.println("平均数为：" + avg);
    }
}
