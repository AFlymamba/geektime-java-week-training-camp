package com.jvm.demo01;

/**
 * 四则运算的例子：移动平均数
 *
 * @author huangyin
 */
public class MovingAverage {
    private int count = 0;
    private double sum = 0.0D;

    /**
     * 累加
     *
     * @param value 待加入值
     */
    public void submit(double value) {
        this.count++;
        this.sum += value;
    }

    /**
     * 计算平均数
     *
     * @return 平均数
     */
    public double getAvg() {
        if (0 == this.count) {
            return sum;
        }
        return this.sum / this.count;
    }
}
