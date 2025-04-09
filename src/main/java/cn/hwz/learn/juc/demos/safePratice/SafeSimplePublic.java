package cn.hwz.learn.juc.demos.safePratice;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/5
 */
public class SafeSimplePublic {


    public void method1(int loopNum) {
        for (int i = 0; i < loopNum; i++) {
            methodSimple();
        }
    }

    public void methodSimple() {
        int i = 0;
        i++;
    }

}
