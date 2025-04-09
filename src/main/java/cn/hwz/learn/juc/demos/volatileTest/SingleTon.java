package cn.hwz.learn.juc.demos.volatileTest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/19
 */
@Slf4j(topic = "c.SingleTon")
public class SingleTon {
    private static volatile SingleTon singleTon= null;

    private SingleTon(){}

    public static SingleTon getSingleTon(){
        if (singleTon == null){
            synchronized (SingleTon.class){
                if (singleTon==null)
                    singleTon = new SingleTon();
            }
        }
        return singleTon;
    }

}
