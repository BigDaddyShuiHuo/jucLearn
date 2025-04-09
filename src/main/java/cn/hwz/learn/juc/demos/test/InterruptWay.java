package cn.hwz.learn.juc.demos.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * @author needleHuo
 * @version jdk11
 * @description 演示优雅的终止线程模式。
 * 这里用监控来举例，业务是：
 * 开启一个线程用来监控。每隔2秒监控一次，并且能随时打断
 * @since 2025/2/25
 */
@Slf4j(topic = "c.InterruptWay")
public class InterruptWay {
    public static void main(String[] args) throws InterruptedException {
        Monitor2 monitor = new Monitor2();
        monitor.start();
        Thread.sleep(7000);
        monitor.stop();

    }
}

@Slf4j(topic = "c.Monitor")
class Monitor {

    private Thread monitorThread = null;

    public void start() {
        monitorThread = new Thread() {
            public void run() {
                while (true) {
                    // 获取打断标志
                    Thread t = Thread.currentThread();
                    boolean interrupted = t.isInterrupted();
                    if (interrupted) {
                        log.debug("打断了，完成收尾工作");
                        return;
                    }

                    try {
                        t.sleep(2000);
                        log.debug("正在监控");
                    } catch (InterruptedException e) {
                        // 这里是有细节的，因为如果线程在sleep状态，打断之后，会把isInterrupted()
                        // 设置为false，代码就不会打断了
                        t.interrupt();
                    }
                }
            }
        };
        monitorThread.start();
    }

    public void stop() {
        if (monitorThread != null)
            // 注意跟Thread.interrupted()的区别，这里不能用Thread.interrupted()
            monitorThread.interrupt();
    }

    public synchronized void test() {
        log.debug("123");
    }


    public static synchronized void test2() {
        log.debug("123");
    }

}


/**
 * 该方法演示使用volatile优化终止模式
 */
@Slf4j(topic = "c.Monitor2")
class Monitor2 {


    private static volatile boolean flag = true;
    private Thread monitorThread = null;

    public void start() {
        monitorThread = new Thread() {
            public void run() {
                while (true) {
                    // 获取打断标志
                    Thread t = Thread.currentThread();
                    if (!flag) {
                        log.debug("打断了，完成收尾工作");
                        return;
                    }
                    try {
                        t.sleep(2000);
                        log.debug("正在监控");
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        monitorThread.start();
    }

    public void stop() {
        if (monitorThread != null) {
            flag = false;
            // 调用这个可以立马打断，不用等循环再进行一轮
            monitorThread.interrupt();
        }

    }

}