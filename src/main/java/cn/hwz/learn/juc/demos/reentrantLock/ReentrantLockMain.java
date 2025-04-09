package cn.hwz.learn.juc.demos.reentrantLock;

import cn.hwz.learn.juc.demos.badLock.Chopstick;
import cn.hwz.learn.juc.demos.badLock.Philosopher;
import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/15
 */
@Slf4j(topic = "c.ReentrantLockMain")
public class ReentrantLockMain {

    public static void main(String[] args) throws InterruptedException {
        //simpleUse();
        reentrant();
        //interruptReentrant();
        //reentrantTryLock();
        //reentrantTryUntilLock();
        //philosopherProblems();
       // awaitTest();

        //cigaretteTest();
    }

    /**
     * 简单使用
     */
    static ReentrantLock lock = new ReentrantLock();

    public static void simpleUse() {
        // 必须保证try，而且保证finally能释放锁
        try {
            lock.lock();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 可重入
     */
    public static void reentrant() {
        // 必须保证try，而且保证finally能释放锁
        try {
            lock.lock();
            methodA();
        } finally {
            lock.unlock();
        }
    }

    public static void methodA() {
        // 必须保证try，而且保证finally能释放锁
        try {
            lock.lock();
            log.debug("methodA");
            methodB();
        } finally {
            lock.unlock();
        }
    }

    public static void methodB() {
        // 必须保证try，而且保证finally能释放锁
        try {
            lock.lock();
            log.debug("methodB");
            methodC();
        } finally {
            lock.unlock();
        }
    }

    public static void methodC() {
        // 必须保证try，而且保证finally能释放锁
        try {
            lock.lock();
            log.debug("methodC");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 可打断
     */
    public static void interruptReentrant() {
        Thread t = new Thread(() -> {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                // 被打断则获取锁失败
                log.debug("获取锁失败");
                throw new RuntimeException(e);
            }
            // 假如获取到锁，他就走这
            try {
                // 这里正常写业务代码
                log.debug("获取锁成功");
            } finally {
                lock.unlock();
            }
        });
        // 主线程先锁住，子线程就会在那边一直尝试
        lock.lock();
        log.debug("主线程先获取锁");
        t.start();
        // 打断线程
        t.interrupt();
        lock.unlock();
    }

    /**
     * 尝试获取锁
     */
    public static void reentrantTryLock() throws InterruptedException {
        Thread t = new Thread(() -> {
            if (lock.tryLock()) {
                try {
                    log.debug("获取锁成功啦");
                } finally {
                    lock.unlock();
                }
            } else {
                log.debug("获取锁失败");
            }
        });
        // 主线程先锁住，子线程就会在那边一直尝试
        lock.lock();
        log.debug("主线程先获取锁");
        t.start();
        // 打断线程
        Thread.sleep(5000);
        lock.unlock();
    }

    /**
     * 超时锁
     */
    public static void reentrantTryUntilLock() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                if (lock.tryLock(6, TimeUnit.SECONDS)) {
                    try {
                        log.debug("获取锁成功啦");
                    } finally {
                        // 如果获取成功，才会进到这个地方，才能unlock，所以unlock不能放到最
                        // 外层的try中
                        lock.unlock();
                    }
                } else {
                    log.debug("获取锁失败");
                }
            } catch (InterruptedException e) {
                log.debug("被打断啦");
                throw new RuntimeException(e);
            }
        });
        // 主线程先锁住，子线程就会在那边一直尝试
        lock.lock();
        log.debug("主线程先获取锁");
        t.start();
        // 打断线程
        Thread.sleep(5000);
        lock.unlock();
    }

    public static void philosopherProblems() {
        ReentrantChopstick c1 = new ReentrantChopstick("筷子1");
        ReentrantChopstick c2 = new ReentrantChopstick("筷子2");
        ReentrantChopstick c3 = new ReentrantChopstick("筷子3");
        ReentrantChopstick c4 = new ReentrantChopstick("筷子4");
        ReentrantChopstick c5 = new ReentrantChopstick("筷子5");

        ReentrantPhilosopher p1 = new ReentrantPhilosopher("哲学家1", c1, c2);
        ReentrantPhilosopher p2 = new ReentrantPhilosopher("哲学家2", c2, c3);
        ReentrantPhilosopher p3 = new ReentrantPhilosopher("哲学家3", c3, c4);
        ReentrantPhilosopher p4 = new ReentrantPhilosopher("哲学家4", c4, c5);
        ReentrantPhilosopher p5 = new ReentrantPhilosopher("哲学家5", c5, c1);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
    }

    static boolean flag1 = true;

    public static void awaitTest() throws InterruptedException {
        Condition condition1 = lock.newCondition();
        Thread t1 = new Thread() {
            public void run() {
                while (flag1) {
                    try {
                        lock.lock();
                        log.debug("线程1获得锁");
                        condition1.await();
                        log.debug("唤醒线程1");
                    } catch (InterruptedException e) {
                        log.debug("线程状态{}", isInterrupted());
                        throw new RuntimeException(e);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };

        t1.start();
        ThreadSleep.sleep(1);
        //t1.interrupt();
        log.debug("状态:{}", lock.isLocked());
    }


    static boolean cigaretteFlag = false;
    static boolean takeOutFlag = false;
    static Condition c1 = lock.newCondition();
    static Condition c2 = lock.newCondition();

    public static void cigaretteTest() throws InterruptedException {
        Thread t1 = new Thread(() -> {

            try {
                lock.lock();
                // 这里用while循环，因为后面用的notifyAll，用while防止被错误叫醒
                while (!cigaretteFlag) {
                    log.debug("没烟干不了活");
                    c1.await();
                }
                log.debug("干活咯");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "小南");
        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                lock.lock();
                while (!takeOutFlag) {
                    log.debug("没外卖干不了活");
                    c2.await();
                }
                log.debug("干活咯");
            } catch (InterruptedException e) {
                log.debug("被老板打断休息啦，快干活");
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "小红");
        t2.start();

        Thread.sleep(3000);
        Thread t3 = new Thread(() -> {
            lock.lock();
            // 这里不能用notify，因为notify是随机的，他可能唤醒的是小红而不是小明;
            log.debug("烟来咯");
            cigaretteFlag = true;
            c1.signalAll();

            log.debug("外卖来咯");
            takeOutFlag = true;
            c2.signalAll();
            lock.unlock();
        }, "小明");
        t3.start();
    }
}
