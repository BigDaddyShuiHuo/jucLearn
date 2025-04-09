package cn.hwz.learn.juc.demos.safePratice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/5
 */
public class SafePublic {

    public void method1(int loopNum) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < loopNum; i++) {
            method2(list);
            method3(list);
        }
    }

    private void method2(List<String> list) {
        list.add("1");
    }

    private void method3(List<String> list) {
        list.remove(0);
    }
}
