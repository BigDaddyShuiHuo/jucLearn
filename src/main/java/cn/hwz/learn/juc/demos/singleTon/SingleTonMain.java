package cn.hwz.learn.juc.demos.singleTon;

import cn.hwz.learn.juc.demos.volatileTest.SingleTon;
import lombok.extern.slf4j.Slf4j;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/19
 */
@Slf4j(topic = "c.SingleTonMain")
public class SingleTonMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
       // reflectTest();
       // serTest();
        InnerSingleTon.getInstance();
    }

    // 反射测试
    public static void reflectTest()  throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
        Class<?> clazz = Class.forName("cn.hwz.learn.juc.demos.singleTon.InnerSingleTon");
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object o = constructor.newInstance();
        log.debug("{}",o);
        log.debug("{}",LazySingleTon.getInstance());
    }

    // 反序列化测试
    public static void serTest() throws IOException {

        InnerSingleTon singleTon = InnerSingleTon.getInstance();
        log.debug("instance1:{}",singleTon.toString());

        // 把类序列化写到文件中
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("lazySingleton.ser"))){
            out.writeObject(singleTon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 读取文件
        InnerSingleTon instance2 = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("lazySingleton.ser"))) {
            instance2 = (InnerSingleTon) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        log.debug("instance1:{}",instance2.toString());
    }
}
