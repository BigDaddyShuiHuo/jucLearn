package cn.hwz.learn.juc.demos.test;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/19
 */
@Slf4j(topic = "c.VolidateTest")
public class VolidateTest {

    public static void main(String[] args) {
        simpleTest();
    }


    public static volatile boolean flag = true;
    public static void simpleTest(){

        Thread t1 = new Thread(()->{
            while(flag){
            }
        });

        t1.start();
        ThreadSleep.sleep(1);
        flag = false;
    }

}
