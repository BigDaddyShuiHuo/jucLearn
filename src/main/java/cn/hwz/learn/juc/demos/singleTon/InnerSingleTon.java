package cn.hwz.learn.juc.demos.singleTon;

import java.io.Serializable;

/**
 * @author needleHuo
 * @version jdk11
 * @description 静态变量的懒汉模式
 * @since 2025/3/19
 */
public final class InnerSingleTon implements Serializable {

    private static class SingleHelper{
        static final InnerSingleTon  single = new InnerSingleTon();
    }

    private InnerSingleTon(){
        if (SingleHelper.single!=null)
            throw new IllegalStateException("对象不为空");
    }

    public static InnerSingleTon getInstance(){
        return SingleHelper.single;
    }

    public Object readResolve(){
        return getInstance();
    }

}
