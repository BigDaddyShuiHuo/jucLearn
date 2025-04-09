package cn.hwz.learn.juc.demos.safePratice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/5
 */
public class SafePublicExtends extends SafePublic{
    public void method3(List<String> list) {
        Thread thread = new Thread(()->list.remove(0));
        thread.start();
    }

}
