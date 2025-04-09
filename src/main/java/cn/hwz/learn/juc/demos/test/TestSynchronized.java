package cn.hwz.learn.juc.demos.test;

import cn.hwz.learn.juc.demos.sellTicket.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author needleHuo
 * @version jdk11
 * @description测试锁机制
 * @since 2025/3/11
 */
@Slf4j(topic = "c.TestSynchronized")
public class TestSynchronized {
    public static void main(String[] args) throws InterruptedException {

     //   sout();
        thinLockTest();
    }



    /**
     * 轻量锁测试
     * @throws InterruptedException
     */
    public static void thinLockTest() throws InterruptedException {
        Ticket paoTea = new Ticket(100);
        Thread.sleep(1000);
        log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
        log.debug("------------------main---------------");
        Thread.sleep(1);
        synchronized (paoTea) {
            log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
        }
        Thread t = new Thread(()->{
            synchronized (paoTea) {
                log.debug("-----------------t1----------------");
                log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
            }
        });
        t.start();
        Thread.sleep(1000);
        log.debug("---------------------------------");
        log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
        synchronized (paoTea){
            Thread.sleep(3000);
            log.debug("---------------我再加一次锁------------------");
            log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
        }
    }


    public static void fatLockTest() throws InterruptedException {
        Ticket paoTea = new Ticket(100);
        Thread.sleep(1000);
        // 一开始还是偏向锁
        log.debug(ClassLayout.parseInstance(paoTea).toPrintable());

        Thread t = new Thread(()->{
            int j = 0;
            synchronized (paoTea) {
                for (int i=0;i<1000;i++){
                    j++;
                }
                log.debug("-----------------t1----------------");
                // 多次竞争变成重量锁了
                log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
            }
        });
        t.start();
        log.debug("------------------main---------------");
        int k = 0;
        synchronized (paoTea) {
            for (int i=0;i<1000;i++){
                k++;
            }
            // 多次竞争变成重量锁了
            log.debug(ClassLayout.parseInstance(paoTea).toPrintable());
        }
        Thread.sleep(1000);
        log.debug("---------------------------------");
        log.debug(ClassLayout.parseInstance(paoTea).toPrintable());

    }

    public static void sout(){
        String hex = "00000146b36e6282";
        long decimal = Long.parseLong(hex,16);
        String binary = Long.toBinaryString(decimal);
        log.debug(binary);
    }
}
