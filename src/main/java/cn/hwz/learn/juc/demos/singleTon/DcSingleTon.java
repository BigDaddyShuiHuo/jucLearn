package cn.hwz.learn.juc.demos.singleTon;

import java.io.Serializable;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/19
 */
public final class DcSingleTon implements Serializable {
    private static volatile DcSingleTon single = null;

    private DcSingleTon(){
        if (single!=null)
            throw new IllegalStateException("对象不为空");
    }

    public DcSingleTon getInstance(){
        if (single==null){
            synchronized (DcSingleTon.class){
                if (single==null)
                    single = new DcSingleTon();
            }
        }
        return single;
    }

    public Object readResolve(){
        return getInstance();
    }
}
