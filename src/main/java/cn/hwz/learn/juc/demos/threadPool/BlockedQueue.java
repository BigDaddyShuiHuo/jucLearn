package cn.hwz.learn.juc.demos.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description 阻塞队列
 * @since 2025/3/25
 */
@Slf4j(topic = "c.BlockedQueue")
public class BlockedQueue<T> {
    // 队列最大容量
    private int queueCapacity;

    // 任务队列,Deque效率比LinkList高
    private volatile Deque<T> taskQueue;
    // 锁
    ReentrantLock lock = new ReentrantLock();
    // 空锁
    Condition emptyConn = lock.newCondition();
    // 满锁
    Condition fullConn = lock.newCondition();


    public BlockedQueue(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        this.taskQueue = new ArrayDeque<>(queueCapacity);
    }

    // 塞进去，无限等待，直到能筛为止
    public void put(T runnable) {
        try {
            lock.lock();
            // 保护性等待
            while (taskQueue.size() >= queueCapacity) {
                fullConn.await();
            }
            log.debug("{}已添加",runnable);
            taskQueue.addLast(runnable);
            emptyConn.signalAll();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    // 有超时时间的筛
    public boolean offset(T runnable,long mills) {
        try {
            long nacos = TimeUnit.MILLISECONDS.toNanos(mills);
            lock.lock();
            // 保护性等待
            while (taskQueue.size() >= queueCapacity) {
                // 该方法如果被signal唤醒，则会返回剩余时间
                nacos = fullConn.awaitNanos(nacos);
                // 队列太多了，应该如何处理，这里需要一个拒绝策略
                if (nacos<=0){
                    log.debug("{}超时了，不添加",runnable);
                    return false;
                }
            }
            log.debug("{}等待超时添加",runnable);
            taskQueue.addLast(runnable);
            emptyConn.signalAll();
            return true;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    // 没有超时时间的获取
    public T take() {
        try {
            lock.lock();
            while (taskQueue.size() <= 0) {
                emptyConn.await();
            }
            T runnable = taskQueue.removeFirst();
            log.debug("{}已取出",runnable);
            fullConn.signalAll();
            return runnable;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    // 有超时时间的获取
    public T poll(long mills) {
        try {
            long nacos = TimeUnit.MILLISECONDS.toNanos(mills);
            lock.lock();
            while (taskQueue.size() <= 0) {
                // 该方法如果被signal唤醒，则会返回剩余时间
                nacos = emptyConn.awaitNanos(nacos);
                if (nacos<=0){
                    return null;
                }
            }
            T runnable = taskQueue.removeFirst();
            log.debug("{}已取出",runnable);
            fullConn.signalAll();
            return runnable;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    // 尝试塞进去，塞不进去就执行拒绝策略
    public void tryPut(T runnable, RejectStrategy rejectStrategy) {
        lock.lock();
        try {
            if (taskQueue.size() == queueCapacity) {
                log.debug("执行拒绝策略：{}",runnable);
                rejectStrategy.reject(this,runnable);
            } else {
                log.debug("无需执行拒绝策略：{}",runnable);
                taskQueue.addLast(runnable);
                emptyConn.signalAll();
            }
        }finally {
            lock.unlock();
        }
    }
}
