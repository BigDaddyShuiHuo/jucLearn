package cn.hwz.learn.juc.demos.test;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/14
 */
public class Message {
    public int getId() {
        return id;
    }

    private int id;

    public Message(int id) {
        this.id = id;
    }
}
