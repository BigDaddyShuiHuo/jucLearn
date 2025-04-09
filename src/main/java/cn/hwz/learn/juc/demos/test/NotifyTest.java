package cn.hwz.learn.juc.demos.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/12
 */
@Slf4j(topic = "c.NotifyTest")
public class NotifyTest {

    /**
     * 定义为final，防止引用改变
     */
    final static Object lock = new Object();
    // 忘记拿烟和外卖
    static boolean cigaretteFlag = false;
    static boolean takeOutFlag = false;


    public static void main(String[] args) throws InterruptedException {
        simpleUsed();
        //interruptWait();
        //notifyAllTest();
        // cigaretteTest();
    }

    /**
     * 简单使用
     */
    public static void simpleUsed() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    log.debug("怠工");
                    lock.wait();
                    log.debug("干活");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(2000);
        log.debug("t1 state:{}",t1.getState());

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("醒醒，快干活了");
                lock.notify();
            }
        }, "t2");
        t2.start();
    }


    /**
     * interrupt
     */
    public static void interruptWait() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    log.debug("怠工");
                    lock.wait();
                    log.debug("干活");
                } catch (InterruptedException e) {
                    log.debug("被老板打断休息啦，快干活");
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
    }


    public static void notifyAllTest() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    log.debug("怠工");
                    lock.wait();
                    log.debug("干活");
                } catch (InterruptedException e) {
                    log.debug("被老板打断休息啦，快干活");
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                try {
                    log.debug("怠工");
                    lock.wait();
                    log.debug("干活");
                } catch (InterruptedException e) {
                    log.debug("被老板打断休息啦，快干活");
                    throw new RuntimeException(e);
                }
            }
        }, "t2");
        t2.start();
        Thread.sleep(1000);
        synchronized (lock) {
            //lock.notify();
            lock.notifyAll();
        }
    }


    public static void cigaretteTest() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    // 这里用while循环，因为后面用的notifyAll，用while防止被错误叫醒
                    while (!cigaretteFlag) {
                        log.debug("没烟干不了活");
                        lock.wait();
                    }
                    log.debug("干活咯");
                } catch (InterruptedException e) {
                    log.debug("被老板打断休息啦，快干活");
                    throw new RuntimeException(e);
                }
            }
        }, "小南");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                try {
                    while (!takeOutFlag) {
                        log.debug("没外卖干不了活");
                        lock.wait();
                    }
                    log.debug("干活咯");
                } catch (InterruptedException e) {
                    log.debug("被老板打断休息啦，快干活");
                    throw new RuntimeException(e);
                }
            }
        }, "小红");
        t2.start();

        Thread.sleep(3000);
        Thread t3 = new Thread(() -> {
            synchronized (lock) {
                // 这里不能用notify，因为notify是随机的，他可能唤醒的是小红而不是小明;
                log.debug("烟来咯");
                cigaretteFlag = true;
                lock.notifyAll();
            }
        }, "小明");
        t3.start();
    }
}
