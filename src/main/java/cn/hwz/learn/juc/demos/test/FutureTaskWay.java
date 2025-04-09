package cn.hwz.learn.juc.demos.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author needleHuo
 * @version jdk11
 * @description 创建线程的第三种方式，FutureTask方式
 * @since 2025/2/21
 */
//给logger定义一个名字,自己写的main方法要输出只能这样
@Slf4j(topic = "c.FutureTask")
public class FutureTaskWay {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 需要传入一个继承Callable接口的实例化对象
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("futureTask is running");
                return 100;
            }
        });
        Thread t1 = new Thread(futureTask,"t1");
        t1.start();
        // 获取线程执行结果
        log.debug("{}",futureTask.get());
    }
}
