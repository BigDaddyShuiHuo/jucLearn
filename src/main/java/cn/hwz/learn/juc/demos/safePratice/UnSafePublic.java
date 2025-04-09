package cn.hwz.learn.juc.demos.safePratice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/5
 */
public class UnSafePublic {

    private List<String> list = new ArrayList<>();

    public void method1(int loopNum) {
        for (int i = 0; i < loopNum; i++) {
            method2();
            method3();
        }
    }

    public void method2() {
        list.add("1");
    }

    public void method3() {
        list.remove(0);
    }
}
