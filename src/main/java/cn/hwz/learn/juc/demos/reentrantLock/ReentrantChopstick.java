package cn.hwz.learn.juc.demos.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description 使用ReentrantLock优化的筷子类
 * @since 2025/3/15
 */
public class ReentrantChopstick extends ReentrantLock {
    private String name;

    public ReentrantChopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chopstick{" +
                "name='" + name + '\'' +
                '}';
    }
}
