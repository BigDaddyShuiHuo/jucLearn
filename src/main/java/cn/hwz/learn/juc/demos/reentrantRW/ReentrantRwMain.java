package cn.hwz.learn.juc.demos.reentrantRW;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description 读写锁
 * @since 2025/4/1
 */
@Slf4j(topic = "c.ReentrantRwMain")
public class ReentrantRwMain {

    public static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    // 读锁
    public static ReentrantReadWriteLock.ReadLock rl = lock.readLock();
    // 写锁
    public static ReentrantReadWriteLock.WriteLock wl = lock.writeLock();

    public static void main(String[] args) {
        testReadWrite();
       // testReadWriteIn();
       // reentrant();
    }

    /**
     * 读-读能共享
     * 读-写互斥
     * 写-写互斥
     */
    public static void testReadWrite() {
        Thread t1 = new Thread(() -> {
            read();
        });
        Thread t2 = new Thread(() -> {
            read();
        });
        t1.start();
        t2.start();
    }

    public static void read() {
        rl.lock();
        try {
            log.debug("start read....{}", Thread.currentThread().getName());
            ThreadSleep.sleep(1);
            log.debug("read..........{}", Thread.currentThread().getName());
        } finally {
            log.debug("read..unlock..{}", Thread.currentThread().getName());
            rl.unlock();
        }

    }

    public static void write() {
        wl.lock();
        try {
            log.debug("start write....{}", Thread.currentThread().getName());
            ThreadSleep.sleep(1);
            log.debug("write.........{}", Thread.currentThread().getName());
        } finally {
            log.debug("write..unlock..{}", Thread.currentThread().getName());
            wl.unlock();
        }
    }

    /**
     * 测试读写锁是否能够共存
     */
    public static void testReadWriteIn() {
        rl.lock();
        wl.lock();
        try {
            log.debug("start write....{}", Thread.currentThread().getName());
            ThreadSleep.sleep(1);
            log.debug("write.........{}", Thread.currentThread().getName());
        } finally {
            log.debug("write..unlock..{}", Thread.currentThread().getName());
            wl.unlock();
            log.debug("rl..unlock..{}", Thread.currentThread().getName());
            rl.unlock();
        }
    }


    /**
     * 可重入
     */
    public static void reentrant() {
        // 必须保证try，而且保证finally能释放锁
        try {
            wl.lock();
            methodA();
        } finally {
            wl.unlock();
        }
    }

    public static void methodA() {
        // 必须保证try，而且保证finally能释放锁
        try {
            wl.lock();
            log.debug("methodA");
            methodB();
        } finally {
            wl.unlock();
        }
    }

    public static void methodB() {
        // 必须保证try，而且保证finally能释放锁
        try {
            wl.lock();
            log.debug("methodB");
            methodC();
        } finally {
            wl.unlock();
        }
    }

    public static void methodC() {
        // 必须保证try，而且保证finally能释放锁
        try {
            wl.lock();
            log.debug("methodC");
        } finally {
            wl.unlock();
        }
    }



}


