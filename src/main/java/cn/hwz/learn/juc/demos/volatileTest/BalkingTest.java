package cn.hwz.learn.juc.demos.volatileTest;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description 犹豫模式
 * @since 2025/3/19
 */
@Slf4j(topic = "c.BalkingTest")
public class BalkingTest {
    public static void main(String[] args) {
        BalkingMonitor balkingMonitor = new BalkingMonitor();
        balkingMonitor.startMonitor();
        ThreadSleep.sleep(1);
        balkingMonitor.startMonitor();
        ThreadSleep.sleep(10);
        balkingMonitor.stopMonitor();
    }
}

@Slf4j(topic = "c.BalkingMonitor")
class BalkingMonitor {

    // 用于开启
    public static volatile boolean START_FLAG = false;
    // 用于终止
    public static volatile boolean STOP_FLAG = false;
    Thread t1 = null;

    public void startMonitor() {
        // 这里不加锁，就会可能会出现两个线程同时监控
        synchronized (this) {
            if (START_FLAG) {
                log.debug("正在运行监控，无需重新开启");
                return;
            }
            START_FLAG = true;
        }
        t1 = new Thread(() -> {
            while (true) {
                if (STOP_FLAG) {
                    log.debug("完成收尾工作");
                    return;
                }
                try {
                    log.debug("正在监控");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        });
        t1.start();
    }

    public void stopMonitor() {
        STOP_FLAG = true;
        START_FLAG = false;
        // 直接打断，少一轮监控
        t1.interrupt();
    }
}
