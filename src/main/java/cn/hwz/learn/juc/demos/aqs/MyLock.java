package cn.hwz.learn.juc.demos.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/29
 */
public class MyLock implements Lock {

    class AqsLock extends AbstractQueuedSynchronizer{

        /**
         * 加锁,acquire参数有什么用的
         * @param acquire the acquire argument. This value is always the one
         *        passed to an acquire method, or is the value saved on entry
         *        to a condition wait.  The value is otherwise uninterpreted
         *        and can represent anything you like.
         * @return
         */
        @Override
        protected boolean tryAcquire(int acquire) {
            if (acquire==1){
                if (compareAndSetState(0,1)){
                    // 设置锁为当前线程独有
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
            }
            return false;
        }

        // 解锁
        @Override
        protected boolean tryRelease(int acquire) {
            if (acquire==1){
                setExclusiveOwnerThread(null);
                // volatile读第一写最后
                setState(0);
                return true;
            }
            return false;
        }

        // 是否加锁了
        @Override
        protected boolean isHeldExclusively() {
            return getState()==1;
        }

        // 返回一个用于等待的条件
        protected Condition newCondition(){
            return new ConditionObject();
        }
    }

    private AqsLock sync = new AqsLock();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
