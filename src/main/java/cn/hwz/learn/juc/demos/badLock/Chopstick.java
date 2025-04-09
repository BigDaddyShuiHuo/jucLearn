package cn.hwz.learn.juc.demos.badLock;

/**
 * @author needleHuo
 * @version jdk11
 * @description 筷子类
 * @since 2025/3/15
 */
public class Chopstick {
    private String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chopstick{" +
                "name='" + name + '\'' +
                '}';
    }
}
