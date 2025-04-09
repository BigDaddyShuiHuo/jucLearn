package cn.hwz.learn.juc.demos.threadPool;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author needleHuo
 * @version jdk11
 * @description 线程池main类
 * @since 2025/3/25
 */
@Slf4j(topic = "c.ThreadMainLearn")
public class ThreadMainLearn {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // newFixedThreadPool();
        //  newCachedThreadPool();
        //  newSingleThreadExecutorTest();
        //   submitMethod();
//        invokeAllMethod();
        //invokeAnyTest();
        //  shutdownTest();
        // shutdownNowTest();
        // TimerTest();
        //  scheduledExecutorTest();
        //  scheduledExecutorTest2();
        //   threadPoolException();
        test();
    }

    /**
     * 我的线程池
     */
    public void myThreadPoolTest() {
        MyThreadPool threadPool = new MyThreadPool(2, 2, 1500,
                (queue, runnable) -> {
                    Runnable r = (Runnable) runnable;
                    r.run();
                    return;
                });
        for (int i = 0; i < 5; i++) {
            int j = i;
            // 这里要new runable，而不用 threadPool.execute(()->{})这种形式的原因是：threadPool.execute(()->{})他用的是
            // 享元模式，对象会一样的，可能是jdk11的新特性
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    ThreadSleep.sleep(2);
                }

                @Override
                public String toString() {
                    return j + "";
                }
            };
            threadPool.execute(r);
        }
    }

    /**
     * newCachedThreadPoolTest
     */
    public static void newFixedThreadPool() {
        AtomicInteger atomicInteger = new AtomicInteger();
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        String namePrefix = "my";
        ExecutorService executorService = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(group, r, namePrefix + atomicInteger.getAndIncrement());
            return t;
        });
        for (int i = 0; i < 2; i++) {
            int j = i;
            executorService.execute(() -> {
                log.debug("哈哈{}", j);
            });
        }

        //   executorService.shutdown();

        executorService.execute(() -> {
            log.debug("哈哈{}", 123);
        });
    }

    /**
     * 演示
     */
    public static void newCachedThreadPool() throws InterruptedException {
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        executorService.execute(()->{
//            log.debug("123456");
//            ThreadSleep.sleep(2);
//        });

        // 同步队列演示
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();

        Thread t = new Thread(() -> {
            try {
                log.debug("123123");
                synchronousQueue.put(1);
                // 与t1同一时间打印
                log.debug("456");

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        ThreadSleep.sleep(1);
        Thread t1 = new Thread(() -> {
            try {
                log.debug("{}", synchronousQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();

        ThreadSleep.sleep(1);
        Thread t2 = new Thread(() -> {
            try {
                //这个放不了,你会发现
                synchronousQueue.put(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t2.start();
    }

    /**
     * newSingleThreadExecutor的演示
     */
    public static void newSingleThreadExecutorTest() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            log.debug("{}", Thread.currentThread().getName());
            int i = 10 / 0;
        });

        ThreadSleep.sleep(1);

        executorService.execute(() -> {
            log.debug("{}", Thread.currentThread().getName());
        });


        executorService.shutdown();
    }

    /**
     * 演示提交方法
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void submitMethod() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<String> submit = executor.submit(() -> {
            log.debug("456");
            ThreadSleep.sleep(2);
            return "123";
        });
        int activeCount = ((ThreadPoolExecutor) executor).getActiveCount();
        // 2秒之后才会打印这条语句，因为get再等待submit返回结果
        log.debug(submit.get());
    }

    /**
     * 批量提交一堆任务
     */
    public static void invokeAllMethod() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        log.debug("start");
        Callable<String> r1 = () -> {
            ThreadSleep.sleep(1);
            return "r1";
        };
        Callable<String> r2 = () -> {
            ThreadSleep.sleep(2);
            return "r2";
        };
        Callable<String> r3 = () -> {
            ThreadSleep.sleep(2);
            return "r3";
        };
        List<Callable<String>> list = Arrays.asList(r1, r2, r3);
        List<Future<String>> futures = executor.invokeAll(list);
        futures.forEach(f -> {
            try {
                log.debug(f.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static void invokeAnyTest() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        log.debug("start");
        Callable<String> r1 = () -> {
            ThreadSleep.sleep(1);
            return "r1";
        };
        Callable<String> r2 = () -> {
            ThreadSleep.sleep(2);
            return "r2";
        };
        Callable<String> r3 = () -> {
            ThreadSleep.sleep(2);
            return "r3";
        };
        List<Callable<String>> list = Arrays.asList(r1, r2, r3);
        String s = executor.invokeAny(list);
        log.debug("{}", s);
    }


    public static void shutdownTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            int j = i;
            executorService.submit(() -> {
                log.debug("哈哈{}", j);
            });
        }
        executorService.shutdown();
        log.debug("end.....");
    }

    public static void shutdownNowTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 3; i++) {

            int j = i;
            executorService.submit(() -> {
                ThreadSleep.sleep(1);
                log.debug("哈哈{}", j);
            });
        }
        List<Runnable> runnables = executorService.shutdownNow();
        log.debug("------------------手动执行的任务------------------");
        runnables.forEach(r -> {
            r.run();
        });
    }


    /**
     * Timer使用
     */
    public static void TimerTest() {
        log.debug("start--------------");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                log.debug("123123");
                int i = 10 / 0;
                ThreadSleep.sleep(1);
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("123123");
            }
        };
        timer.schedule(task, 1000);
        timer.schedule(task2, 1000);
    }

    /**
     * 线程池定时任务
     * 1.并行
     * 2.第一个出现异常不会影响第二个执行
     */
    public static void scheduledExecutorTest() {
        log.debug("start--------------");
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        scheduled.schedule(() -> {
            int i = 10 / 0;
            log.debug("123");
            ThreadSleep.sleep(1);
        }, 1, TimeUnit.SECONDS);

        scheduled.schedule(() -> {
            log.debug("456");
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * 演示: scheduleAtFixedRate 与 scheduleWithFixedDelay
     */
    public static void scheduledExecutorTest2() {
        log.debug("start--------------");
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        /**
         * 第一个参数：
         * runnable
         * 初始延时时间
         * 间隔时间
         * 时间单位
         */
        scheduled.scheduleAtFixedRate(() -> {
            log.debug("123");
        }, 1, 1, TimeUnit.SECONDS);

        /**
         * 参数与上面那个一样
         * 但是这个的区别时需要等上一个任务运行完才进行下一个任务
         */
        scheduled.scheduleWithFixedDelay(() -> {
            log.debug("123");
            ThreadSleep.sleep(2);
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 演示线程池的异常处理
     */
    public static void threadPoolException() throws ExecutionException, InterruptedException {
        // 第一种方式，手动try-catch
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            try {
                log.debug("123");
                int i = 10 / 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 第二种方式，使用get，代码出现异常，get的时候会抛出，推荐使用这种方式
        Future<Object> submit = executorService.submit(() -> {
            int i = 10 / 0;
            return true;
        });
        submit.get();
    }

    public static void test() throws ExecutionException, InterruptedException {
        FutureTask<String> task = new FutureTask<>(() -> {
            int i = 10 / 0;
            return "123";

        });
        task.run();
        //log.debug("{}",task.get());
    }


}
