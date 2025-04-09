package cn.hwz.learn.juc.demos.countdownLatch;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/4/3
 */
@Slf4j(topic = "c.CountDownLatchMain")
public class CountDownLatchMain {


    public static void main(String[] args) throws InterruptedException {
        //simpleUse();
        // threadSum();
        loadingPractice();
    }

    /**
     * 简单使用
     * 调用一次countDown就会减1，直到完全减为0，await才结束
     *
     * @throws InterruptedException
     */
    public static void simpleUse() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        log.debug("starting..............");
        new Thread(() -> {
            log.debug("running............");
            ThreadSleep.sleep(3);
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
        log.debug("end..............");
    }

    /**
     * 汇总练习，主线程把所有线程执行完之后，才会汇总成最终结果
     */
    public static void threadSum() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        log.debug(".........start.......");
        for (int i = 0; i < 5; i++) {
            int j = i;
            executorService.submit(() -> {
                ThreadSleep.sleep(1);
                log.debug("{}进行了计算", j);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.debug("****汇总******");
        executorService.shutdown();
    }

    /**
     * 模拟游戏进度打印
     */
    public static void loadingPractice() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        String[] array = new String[10];
        for (int i = 0; i < 10; i++) {
            // 这个不能写在lambda表达式里面
            int k = i;
            executorService.submit(() -> {
                for (int j = 0; j <= 100; j++) {
                    ThreadSleep.ranSleep(300);
                    array[k] = j + "%";
                    // \r是回车符，拼接这个输出等于回退到上一行输出，也就是把上一行输出覆盖掉
                    System.out.print("\r" + Arrays.toString(array));
                }
                // countDownLatch位置要写在循环外，因为一个玩家进度到了100%才-1
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.debug("\n------------游戏开始----------");
        executorService.shutdown();
    }

    /**
     * 汇总操作进行两次
     * @throws InterruptedException
     */
    public static void loopCountDown() throws InterruptedException {
        for (int i=0;i<3;i++) {
            CountDownLatch countDownLatch = new CountDownLatch(2);
            log.debug("starting..............");
            new Thread(() -> {
                log.debug("running............");
                ThreadSleep.sleep(3);
                countDownLatch.countDown();
            }).start();
            new Thread(() -> {
                log.debug("running............");
                ThreadSleep.sleep(3);
                countDownLatch.countDown();
            }).start();
            countDownLatch.await();
            log.debug("end..............");
        }
    }
}
