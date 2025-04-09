package cn.hwz.learn.juc.demos.unchange;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/23
 */
@Slf4j(topic = "c.UnchangeMain")
public class UnchangeMain {
    public static void main(String[] args) {
        connectPoolTest();

      //  test();
    }

    public static void  dateFormatter(){
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Date date = new Date();
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(() -> {
                // 恶心。真tm难看
                LocalDate d = sdf.parse("1951-01-01", LocalDate::from);
                LocalDate localDate = sdf.parse("1951-01-01", temporal -> LocalDate.from(temporal));
            });
            t.start();
        }
    }


    public static void connectPoolTest(){
        ConnectionPool connectionPool = new ConnectionPool();
        for (int i =0;i<5;i++){
            new Thread(()->{
                MyConnection connection = connectionPool.get();
                ThreadSleep.sleep(1);
                connectionPool.back(connection);
            }).start();
        }
    }

    static ReentrantLock lock = new ReentrantLock();
    public static void test(){
        for (int i =0;i<5;i++){
            new Thread(()->{
                lock.lock();
                log.debug("{}获取到锁",Thread.currentThread());
            }).start();
        }
    }
}
