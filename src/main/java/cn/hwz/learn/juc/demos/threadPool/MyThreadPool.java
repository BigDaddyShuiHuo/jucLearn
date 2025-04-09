package cn.hwz.learn.juc.demos.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description 我的线程池
 * @since 2025/3/25
 */
@Slf4j(topic = "c.MyThreadPool")
public class MyThreadPool {

    // 线程数组
    private List<Work> threadList;
    // 最大线程数
    private int threadCapacity;
    // 时间单元
    private long pollMills;
    // 任务队列
    private BlockedQueue<Runnable> blockedQueue;
    // 拒绝策略
    private RejectStrategy rejectStrategy;


    public MyThreadPool(int threadCapacity, int queueCapacity, long pollMills,RejectStrategy rejectStrategy) {
        this.threadCapacity = threadCapacity;
        this.threadList = new ArrayList<>(threadCapacity);
        this.blockedQueue = new BlockedQueue<>(queueCapacity);
        this.pollMills = pollMills;
        this.rejectStrategy = rejectStrategy;
    }

    // 执行方法
    public void execute(Runnable runnable) {
        // 这里要用synchronized，因为如果用可重入锁的话，他await会释放锁，这时其他线程拿到锁，就会跳过threadList.size() < threadCapacity这个判断·
        synchronized (threadList) {
            // 判断线程数有没有到达最大线程数，没有则new，而且需要直接开启线程
            if (threadList.size() < threadCapacity) {
                blockedQueue.put(runnable);
                Work work = new Work(runnable);
                work.start();
                threadList.add(work);
            }else {
                blockedQueue.tryPut(runnable,rejectStrategy);
            }
        }
    }


    private class Work extends Thread {

        // 初始化第一个任务
        public Runnable task;

        public Work(Runnable task) {
            this.task = task;
        }

        @Override
//        public void run() {
//            while (true) {
//                Runnable take;
//                take = blockedQueue.take();
//                log.debug("{}正在运行",take);
//                take.run();
//            }
//        }
        // 就是有两层，execute start后调用take的run方法
        public void run() {
            // 尝试获取，超时都没获取到就会返回null
            while ( (task = blockedQueue.poll(pollMills)) != null) {
                log.debug("{}正在运行", task);
                task.run();
            }

            // 最后超过等待时间还是获取到null，证明线程数太多了，这里我写了remove 0.要写remove this
            if (!threadList.isEmpty()) {
                log.debug("太多了，需要削减");
                threadList.remove(this);
            }
        }
    }

}
