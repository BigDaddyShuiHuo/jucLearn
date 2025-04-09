package cn.hwz.learn.juc.demos.test;

import ch.qos.logback.core.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/2/21
 */
//给logger定义一个名字,自己写的main方法要输出只能这样
@Slf4j(topic = "c.ThreadWay")
public class ThreadWay {
    public static void main(String[] args) throws InterruptedException {
//        Date date = new Date();
//        Thread t = new Thread(){
//            @Override
//            public void run() {
//                log.debug("t1 running");
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                log.debug("t1 stop");
//            }
//        };
//        t.setName("t1");
//        t.start();
//        Thread.sleep(500);
//        log.debug("{},t1", t.getState());
//        t.interrupt();
//        log.debug("main stop");
        testPark();
    }

    public static void testPark() throws InterruptedException {
        Thread t = new Thread(){
            public void run() {
                log.debug("t1 park");
                LockSupport.park();
                log.debug("state:{}",getState());
                //park被打断后，太是不会重置isInterrupted的，
                // 为true，导致下一个park不生效，所以我这里为了让下一个生效，使用了Thread.interrupted()
                // 获取状态，这个方法获取状态后会把isInterrupted改为false，下一个park就生效了
               // Thread.interrupted();
            //    log.debug("state:{}",isInterrupted());
               // LockSupport.park();
                log.debug("t1 running");
            }
        };
        t.start();
        Thread.sleep(2000);
        LockSupport.unpark(t);
        log.debug("---{}",t.isInterrupted());
        log.debug("unpark");
    }

    public void showThreadMethod(Thread t1) throws InterruptedException {
        // 启动线程，注意跟t1.run的区别，t1.run还是同步
        t1.start();
        // 获取线程状态
        t1.getState();
        // 线程睡眠，可以用t1.interrupt()终止，唤醒后抛出异常，线程此时变成会timed_waiting状态
        // 一般很少用，有些服务器开发中会用到while(true){sleep},因为要让线程进入等待状态，但不加sleep
        // cpu一直空转就会达到100%占用
        Thread.sleep(1000);
        // 让出线程的执行权，进入就绪状态，但是结果能否让出，还是得看cpu。因为让出去是就绪状态，cpu还有可能
        // 立马调度你
        Thread.yield();

        // 设置线程优先级，他是提示任务调度器先执行这个线程，但是任务调度器可以忽略这个提示，所以不一定生效
        t1.setPriority(Thread.MAX_PRIORITY);
        // ti.join().等待t1执行完之后再继续向下执行，join可以传入参数进行限时等待，表示超过这个时间就不等了
        t1.join();
        // 终止线程，该方法的逻辑是给线程设置终止标志位（isInterrupt为true），如果线程处于sleep，wait，join，
        // 会强制终止并抛出异常，且isinterrupted()会为false，假如线程还没运行完，则会继续运行，如果想要打断
        // ，则自己在线程中加入，且isinterrupted()判断是否终止，如果终止直接return掉，与stop的区别
        t1.interrupt();
        // 获取打断标记，并且不会清楚打断标记。注意Thread.interrupted()也是获取打断标记，但会清除打断标记
        t1.isInterrupted();
        // 设置为守护线程，当非守护线程完成工作时，无论守护线程是否完成工作，都将终止
        // java的垃圾回收就是守护线程
        t1.setDaemon(true);

    }
}
