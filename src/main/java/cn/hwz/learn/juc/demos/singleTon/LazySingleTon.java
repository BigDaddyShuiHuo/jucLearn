package cn.hwz.learn.juc.demos.singleTon;

import java.io.Serializable;

/**
 * @author needleHuo
 * @version jdk11
 * @description 懒汉模式
 * @since 2025/3/19
 */
public final class LazySingleTon implements Serializable {

    public final static LazySingleTon single = new LazySingleTon();

    private LazySingleTon() {
        // 防止反射创建对象
        if (single!=null)
            throw new IllegalStateException("对象不为空");
    }

    public static LazySingleTon getInstance(){
        return single;
    }


    // 防止反序列化
    public Object readResolve(){
        return single;
    }

}
