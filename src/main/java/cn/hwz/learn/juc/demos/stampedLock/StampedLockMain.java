package cn.hwz.learn.juc.demos.stampedLock;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description StampedLock使用
 * @since 2025/4/3
 */
@Slf4j(topic = "c.StampedLockMain")
public class StampedLockMain {


    static StampedLock stampedLock = new StampedLock();

    public static void main(String[] args) {
       // simpleUsed();
        testLockLevel();
    }

    /**
     * StampedLock的简单使用
     */
    public static void simpleUsed(){
        Thread t0 = new Thread(()->{
            read();;
        });
        t0.start();

       // ThreadSleep.sleep(1);
        Thread t1 = new Thread(()->{
            read();
        });
        t1.start();
    }


    public static void read(){
        long stamp = stampedLock.readLock();
        log.debug("reading....{}",Thread.currentThread().getName());
        stampedLock.unlockRead(stamp);
    }

    public static void write(){
        long stamp = stampedLock.writeLock();
        log.debug("writing.....");
        ThreadSleep.sleep(2);
        stampedLock.unlockWrite(stamp);
    }


    /**
     * 简单嵌套有问题
     */
    public static void unReadIn(){
        long stamp = stampedLock.writeLock();
        long l = stampedLock.readLock();
        log.debug("writing.....");
        ThreadSleep.sleep(2);
        stampedLock.unlockRead(l);
        stampedLock.unlockWrite(stamp);
    }

    /**
     * 测试锁升级
     */
    public static void testLockLevel(){
        new Thread(()->{
            lockLevel();
        }).start();

        ThreadSleep.sleep(1);
        // 来一个修改过的，看看会不会锁升级
        new Thread(()->{
           write();
        }).start();
    }

    /**
     * 演示乐观锁升级
     */
    public static void lockLevel(){
        long stamp = stampedLock.tryOptimisticRead();
        // 处理数据
        log.debug("...数据处理...");
        ThreadSleep.sleep(5);
        // 返回true表示如果没有人动过数据
        if (stampedLock.validate(stamp)){
            log.debug("没有人修改过数据，直接返回");
        }

        // 有人修改过数据，锁升级
        long writeLock = stampedLock.writeLock();
        log.debug(".....处理数据....");
        stampedLock.unlockWrite(writeLock);
    }
}
