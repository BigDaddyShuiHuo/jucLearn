package cn.hwz.learn.juc.demos.reentrantLock;

import cn.hwz.learn.juc.demos.badLock.Chopstick;
import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author needleHuo
 * @version jdk11
 * @description 使用可重入锁进行优化的哲学家类
 * @since 2025/3/15
 */
@Slf4j(topic = "c.Philosopher")
public class ReentrantPhilosopher extends Thread{

    private final ReentrantChopstick left;
    private final ReentrantChopstick right;

    public ReentrantPhilosopher(String name, ReentrantChopstick left, ReentrantChopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while(true){
            getLeft();
        }
    }

    private void getLeft(){
        try {
            if (left.tryLock(1, TimeUnit.SECONDS)){
                try {
                    getRight();
                }finally {
                    left.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void getRight() throws InterruptedException {
        if (right.tryLock(1, TimeUnit.SECONDS)) {
            try {
                log.debug("{}在吃饭", getName());
                ThreadSleep.sleep(0.5);
            }finally {
                right.unlock();
            }
        }
    }
}
