package cn.hwz.learn.juc.demos.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description 创建线程的第二种方式，runnable方式
 * @since 2025/2/21
 */
//给logger定义一个名字,自己写的main方法要输出只能这样
@Slf4j(topic = "c.Runnable")
public class RunnableWay {
    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("t1 is running");
            }
        });
        t1.setName("t1");
        t1.start();
    }
}
