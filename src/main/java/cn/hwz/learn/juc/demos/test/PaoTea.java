package cn.hwz.learn.juc.demos.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description 模拟泡茶练习
 * 泡茶有以下步骤：
 * 1.洗茶壶
 * 2.洗茶杯
 * 3.洗水壶
 * 4.烧开水
 * 5.拿茶叶
 * 6.泡茶
 * 怎么安排最快
 * @since 2025/2/26
 */
@Slf4j(topic = "c.PaoTea")
public class PaoTea {
    /**
     * 接触到这种问题的时候，首先我们要分析，有什么动作是强关联关系
     * 显然，只有洗水壶跟烧开水是强关联关系，必须先洗茶壶在烧开水，其他都没有谁先谁后之间关系
     * 那么如何安排线程才最好呢？
     * 这时就要分析单个任务大概时间，假如这里烧开水是15分钟，其他都是1分钟。
     * 设置5个线程显然是浪费的，因为所有线程其实都在等待烧开水完成
     * 所以 洗茶壶+烧开水 一个线程16分钟左右，其他一个线程 5分钟左右
     */

    public static void main(String[] args) {
        easyMakeTea();
    }

    /**
     * 简易泡茶模式
     */
    public static void easyMakeTea(){
        Thread t1 = new Thread(){
            public void run(){
                log.debug("洗水壶");
                mySleep(1);
                log.debug("烧水");
                mySleep(15);
            }
        };

        Thread t2 = new Thread(){
            public void run(){
                log.debug("洗茶壶");
                mySleep(1);
                log.debug("洗茶杯");
                mySleep(1);
                log.debug("拿茶叶");
                mySleep(1);
                //这里要等带t1烧开谁后才能泡茶
                try {
                    t1.join();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("泡茶");
                mySleep(1);
            }
        };
        t1.start();
        t2.start();

        /**
         * 这个简易泡茶实际上存在两个问题
         * 1.t1与t2在实际运行中，只能大概估计t1比t2慢，但是假如茶叶不知道丢哪里去了，导致找了很长时间
         * 这就导致t2比t1慢了，这时情况就变成t1要等t2了，所以程序最后设置成互相等待
         * 2.t2技术不行，要t1泡茶，那就涉及到t2拿茶叶给t1泡，所以t1与t2要设置成线程间的相互通信
         */
    }

    /**
     * 自定义睡眠
     * @param i
     */
    public static void mySleep(long i){
        try {
            Thread.sleep(i*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
