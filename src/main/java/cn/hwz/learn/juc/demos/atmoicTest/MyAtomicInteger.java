package cn.hwz.learn.juc.demos.atmoicTest;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/23
 */
public final class MyAtomicInteger {

    private volatile int value;
    private static final Unsafe unsafe;
    private static final long offset;

    static {
        Field theUnsafe = null;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            offset = unsafe.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAndIncrease() {
        int oldValue;
        int newValue;
        while (true) {
            oldValue = value;
            newValue = oldValue+1;
            if (unsafe.compareAndSwapInt(this,offset,value,++value)){
                break;
            }
        }
    }


    public int getValue() {
        return value;
    }
}
