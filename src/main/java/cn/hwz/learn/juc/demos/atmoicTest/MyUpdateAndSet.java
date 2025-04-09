package cn.hwz.learn.juc.demos.atmoicTest;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/20
 */
@FunctionalInterface
public interface MyUpdateAndSet {

    int apply(int num);
}
