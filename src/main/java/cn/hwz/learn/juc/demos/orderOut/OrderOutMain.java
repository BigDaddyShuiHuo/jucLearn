package cn.hwz.learn.juc.demos.orderOut;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/16
 */
@Slf4j(topic = "c.OrderOutMain")
public class OrderOutMain {
    public static void main(String[] args) {
        // waitNotifyOrder();
        // awaitSignalOrder();
        // parkOrder();
        // alternateOut();
        // alternateAwaitOut();
        alternateParkOut();
    }

    /**
     * wait notify的顺序输出
     */
    static Object obj = new Object();
    static boolean t1Flag = false;

    public static void waitNotifyOrder() {
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                while (!t1Flag) {
                    try {
                        obj.wait();
                        ThreadSleep.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("t1");
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (obj) {
                log.debug("t2");
                t1Flag = true;
                obj.notifyAll();
            }
        });
        t1.start();
        t2.start();
    }


    /**
     * await/signal方式顺序输出
     */
    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void awaitSignalOrder() {
        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                while (!t1Flag) {
                    try {
                        condition.await();
                        ThreadSleep.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("t1");
            } finally {
                lock.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                lock.lock();
                log.debug("t2");
                t1Flag = true;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        });
        t1.start();
        t2.start();
    }

    /**
     * park和unpark方式顺序输出
     */
    public static void parkOrder() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("t1");
        });

        Thread t2 = new Thread(() -> {
            log.debug("t2");
            t1Flag = true;
            LockSupport.unpark(t1);
        });
        t1.start();
        t2.start();
    }

    public static void alternateOut(){
        AlternateOutWait alternateOutWait = new AlternateOutWait(1);
        Thread t1 = new Thread(()->{
           alternateOutWait.print(1,2,"a");
        });

        Thread t2 = new Thread(()->{
            alternateOutWait.print(2,3,"b");
        });

        Thread t3 = new Thread(()->{
            alternateOutWait.print(3,1,"c");
        });

        t3.start();
        t2.start();
        t1.start();
    }

    public static void alternateAwaitOut(){
        AlternateOutAwait alternateOutWait = new AlternateOutAwait(1);
        Thread t1 = new Thread(()->{
            alternateOutWait.print(1,2,"a");
        });

        Thread t2 = new Thread(()->{
            alternateOutWait.print(2,3,"b");
        });

        Thread t3 = new Thread(()->{
            alternateOutWait.print(3,1,"c");
        });

        t3.start();
        t2.start();
        t1.start();
    }

    static Thread t1 = null;
    static Thread t2 = null;
    static Thread t3 = null;
    public static void alternateParkOut(){
        AlternateOutPark alternateOutPark = new AlternateOutPark();

        t1 = new Thread(()->{
            alternateOutPark.print(t2,"a");
        });

        t2 = new Thread(()->{
            alternateOutPark.print(t3,"b");
        });

        t3 = new Thread(()->{
            alternateOutPark.print(t1,"c");
        });

        t3.start();
        t2.start();
        t1.start();

        LockSupport.unpark(t1);
    }
}




