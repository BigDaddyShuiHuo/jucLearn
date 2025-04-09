package cn.hwz.learn.juc.demos.safePratice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/5
 */
public class Main {
    // 线程数量
    private final static int THREAD_NUM = 2;
    // 循环数量
    private final static int LOOP_NUM = 200;


    public static void main(String[] args) {

        SafePublicExtends obj = new SafePublicExtends();
        for (int i = 0;i<THREAD_NUM;i++) {
            Thread thread = new Thread(() -> obj.method1(LOOP_NUM));
            thread.start();
        }

    }

//    public static void main(String[] args) {
//        SafePublic obj = new SafePublic();
//        for (int i = 0;i<THREAD_NUM;i++) {
//            Thread thread = new Thread(() -> obj.method1(LOOP_NUM));
//            thread.start();
//        }
//        List<String> list = new ArrayList<>();
//        Thread t3 = new Thread(() -> obj.method3(list));
//    }

//    public static void main(String[] args) {
//        SafePublic obj = new SafePublic();
//        for (int i = 0;i<THREAD_NUM;i++) {
//            Thread thread = new Thread(() -> obj.method1(LOOP_NUM));
//            thread.start();
//        }
//    }

//    public static void main(String[] args) {
//        SafeSimplePublic safeSimplePublic = new SafeSimplePublic();
//        for (int i = 0;i<THREAD_NUM;i++) {
//            Thread thread = new Thread(() -> safeSimplePublic.method1(LOOP_NUM));
//            thread.start();
//        }
//    }

//    public static void main(String[] args) {
//        UnSafePublic obj = new UnSafePublic();
//        for (int i = 0;i<THREAD_NUM;i++) {
//            Thread thread = new Thread(() -> obj.method1(LOOP_NUM));
//            thread.start();
//        }
//    }
}
