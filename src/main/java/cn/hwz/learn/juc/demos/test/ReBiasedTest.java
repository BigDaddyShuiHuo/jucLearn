package cn.hwz.learn.juc.demos.test;

import cn.hwz.learn.juc.demos.sellTicket.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * @author needleHuo
 * @version jdk11
 * @description 重偏向测试
 * @since 2025/3/11
 */
@Slf4j(topic = "c.ReBiasedTest")
public class ReBiasedTest {
    public static void main(String[] args) throws InterruptedException {
        // test1();
        // test2();
        // test3();
        test4();
    }

    /**
     * 当另外一个线程处于不活跃状态下的重偏向
     */
    public static void test1() throws InterruptedException {
        Ticket lock = new Ticket(100);
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                log.debug(ClassLayout.parseInstance(lock).toPrintable());
            }
        });
        t1.start();
        t1.join();
        // 通知系统调用垃圾回收机制后，睡个3秒，等jvm更新线程状态
        System.gc();
        Thread.sleep(3000);
        log.debug("-----------------------------------------------");
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug(ClassLayout.parseInstance(lock).toPrintable());
            }
        });
        t2.start();
        t2.join();
    }


    public static void test2() {
        final int time = 30;
        Vector<Ticket> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < time; i++) {
                Ticket t = new Ticket(100);
                list.add(t);
                synchronized (t) {
                    log.debug("---------------t1-{}---------------", i);
                    log.debug(ClassLayout.parseInstance(t).toPrintable());
                }
            }
            // 这里用notify是为了唤醒t2线程，为什么要这么用，后面再看
            synchronized (list) {
                list.notify();
            }
        });
        t1.start();


        Thread t2 = new Thread(() -> {
            synchronized (list) {
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int i = 0; i < time; i++) {
                Ticket t = list.get(i);
                synchronized (t) {
                    log.debug("---------------t2-{}---------------", i);
                    log.debug(ClassLayout.parseInstance(t).toPrintable());
                }
            }
        });
        t2.start();
    }

    static Thread t1, t2, t3;

    public static void test3() {
        final int time = 40;
        Vector<Ticket> list = new Vector<>();
        t1 = new Thread(() -> {
            for (int i = 0; i < time; i++) {
                Ticket t = new Ticket(100);
                list.add(t);
                synchronized (t) {
                    log.debug("---------------t1-{}---------------", i);
                    log.debug(ClassLayout.parseInstance(t).toPrintable());
                }
            }
            LockSupport.unpark(t2);
        });
        t1.start();

        t2 = new Thread(() -> {
            LockSupport.park();
            for (int i = 0; i < time; i++) {
                Ticket t = list.get(i);
                synchronized (t) {
                    log.debug("---------------t2-{}---------------", i);
                    log.debug(ClassLayout.parseInstance(t).toPrintable());
                }
            }
            LockSupport.unpark(t3);
        });
        t2.start();

        t3 = new Thread(() -> {
            LockSupport.park();
            for (int i = 0; i < time; i++) {
                Ticket t = list.get(i);
                synchronized (t) {
                    log.debug("---------------t3-{}---------------", i);
                    log.debug(ClassLayout.parseInstance(t).toPrintable());
                }
            }
        });
        t3.start();
    }


    public static void test4() throws InterruptedException {
        final Object lock = new Object();
        for (int i = 0; i < 50; i++) {
            Thread t = new Thread(() -> {
                synchronized (lock) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.debug(ClassLayout.parseInstance(lock).toPrintable());
                }
            });
            t.start();
            t.join();
        }
    }
}
