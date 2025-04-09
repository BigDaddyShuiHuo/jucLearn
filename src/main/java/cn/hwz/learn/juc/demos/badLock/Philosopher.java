package cn.hwz.learn.juc.demos.badLock;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description 哲学家类
 * @since 2025/3/15
 */
@Slf4j(topic = "c.Philosopher")
public class Philosopher extends Thread{

    private final Chopstick left;
    private final Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while(true){
            synchronized (left){
                synchronized (right){
                    log.debug("{}在吃饭",getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
