package cn.hwz.learn.juc.demos.aqs;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/29
 */
@Slf4j(topic = "c.AqsMain")
public class AqsMain {
    public static void main(String[] args) {
        aqsTest();
    }

    static MyLock lock = new MyLock();
    public static  void aqsTest(){
        Thread t1 = new Thread(()->{
            lock.lock();
            try{
                log.debug("123123");
                ThreadSleep.sleep(2);
            }finally {
                log.debug("unlock....");
                lock.unlock();
            }
        });
        t1.start();
        ThreadSleep.sleep(1);
        Thread t2 = new Thread(()->{
            lock.lock();
            try{
                log.debug("456");
            }finally {
                log.debug("unlock....");
                lock.unlock();
            }
        });
        t2.start();
    }
}
