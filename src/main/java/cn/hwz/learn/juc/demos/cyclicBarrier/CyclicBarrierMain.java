package cn.hwz.learn.juc.demos.cyclicBarrier;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/4/3
 */
@Slf4j(topic = "c.CyclicBarrierMain")
public class CyclicBarrierMain {
    public static void main(String[] args) throws InterruptedException {
        //simpleUse();
        //endRunnable();
        loopCyclicBarrier();
    }

    public static void simpleUse(){
        // 一次任务需要完成2次等待
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        log.debug("starting");
        new Thread(() -> {
            try {
                log.debug("starting.......{}",Thread.currentThread().getName());
                cyclicBarrier.await();
                log.debug("running.......{}",Thread.currentThread().getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                log.debug("starting.......{}",Thread.currentThread().getName());
                ThreadSleep.sleep(2);
                cyclicBarrier.await();
                log.debug("running.......{}",Thread.currentThread().getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    public static void endRunnable(){
        // await结束后runnable方法里面运行
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2,()->{
            log.debug("一轮结束啦！！！");
        });
        log.debug("starting");
        new Thread(() -> {
            try {
                log.debug("starting.......{}",Thread.currentThread().getName());
                cyclicBarrier.await();
                log.debug("running.......{}",Thread.currentThread().getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                log.debug("starting.......{}",Thread.currentThread().getName());
                ThreadSleep.sleep(2);
                cyclicBarrier.await();
                log.debug("running.......{}",Thread.currentThread().getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    /**
     * 循环执行线程
     * @throws InterruptedException
     */
    public static void loopCyclicBarrier() throws InterruptedException {
        // 注意这个线程池数量要与cyclicBarrier中的parties一样
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        for (int i=0;i<3;i++) {
            executorService.submit(() -> {
                log.debug("running............{}",Thread.currentThread().getName());
                ThreadSleep.sleep(3);
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            executorService.submit(() -> {
                log.debug("running............{}",Thread.currentThread().getName());
                ThreadSleep.sleep(3);
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


}
