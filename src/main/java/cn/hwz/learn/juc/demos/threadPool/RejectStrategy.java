package cn.hwz.learn.juc.demos.threadPool;

/**
 * @author needleHuo
 * @version jdk11
 * @description 拒绝策略
 * @since 2025/3/25
 */
@FunctionalInterface
public interface RejectStrategy<T> {
    // 这里需要传入队列是因为要执行队列的put和offset方法
    public void reject(BlockedQueue<T> queue,T runnable);
}
