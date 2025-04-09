package cn.hwz.learn.juc.demos.tool;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description 线程间的通信，用于一个线程要等另外一个线程的结果，只能使用一次，且不能共有
 * @since 2025/3/12
 */
@Slf4j(topic = "c.GuardedObj")
public class GuardedObj {




    private Object result = null;


    /**
     * 用于获取线程结果
     *
     * @return
     */
    public Object get(long millsTime) throws InterruptedException {
        // 锁是this
        synchronized (this) {
            long startTime = System.currentTimeMillis();
            long pastTime = 0;
            // 用while来做包保护性等待
            while (result == null) {
                if (pastTime >= millsTime) {
                    break;
                }
                // 如果pastTime在这行代码之前计算，如果刚好在边界值上，这个等待时间就不准确了
                // 因为第一次已经消耗的时间应该是0才对
                this.wait(millsTime - pastTime);
                // pastTime要在最后再计算，减少误差
                pastTime = System.currentTimeMillis() - startTime;
            }
            return result;
        }
    }
    public void complete(Object result) {
        synchronized (this) {
            this.result = result;
            // 唤醒所有线程
            this.notifyAll();
        }
    }


}
