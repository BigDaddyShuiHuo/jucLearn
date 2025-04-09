package cn.hwz.learn.juc.demos.atmoicTest;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import cn.hwz.learn.juc.demos.transfer.Account;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/20
 */
@Slf4j(topic = "c.AtomicMain")
public class AtomicMain {
    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        //atomicReferenceTest();
        // AToBToA();
        // atomicArrayTest();
        //atomicField();
//        for (int i=0;i<5;i++) {
//            longAddrCompareTest();
//            longAddrTest();
//        }
       // unSafeTest();
        myAtomicTest();
    }


    public static void noLockTransfer() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicAccount atomicAccount = new AtomicAccount(10000);
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                atomicAccount.transfer(10);
            });
            list.add(t);
        }

        for (Thread t : list) {
            t.start();
        }

        for (Thread t : list) {
            t.join();
        }

        log.debug("{}", atomicAccount.getMoney());
        log.debug("耗时：{}ms", System.currentTimeMillis() - start);
    }


    /**
     * AtomicInteger
     * AtomicLong
     * AtomicBoolean
     */
    public static void atomicMethod() {
        AtomicInteger in = new AtomicInteger(10);
        // cas更新，原来的值是10,更新后的是100，返回true更新成功，false更新失败
        in.compareAndSet(10, 100);
        // 相当于不断cas的++i(先++，后返回)操作，他底层也是用了compareAndSet这个方法，相当于他帮我们封装了，不用我们自己写while{compareAndSet}
        in.incrementAndGet();
        // 跟上面那个相反，先返回，后++，也是不断cas直到成功
        in.getAndIncrement();
        // Increment的增加,decrement是减少的意思，相当于不断cas,进行--i操作
        in.decrementAndGet();
        // 不断cas的i--
        in.getAndIncrement();
        // 上面的操作只能不断减-，这个方法可以不止+1
        in.getAndAdd(10);
        in.addAndGet(10);
        // 假如我要想要完成10的次方，上面那些加减肯定不满足，这时候要传入一个表达式,这里的x实际上就是in.get()，
        // 因为是函数式编程，要实现的方法需要一个传参，x就是传参。我们看他源码可以发现x其实就是in.get()
        in.updateAndGet(x -> {
            return 10 * x;
        });
        // 等于这里可以传一个参，上面那个实际上没传参的，可以实现循环累乘
        in.accumulateAndGet(10, (y, x) -> {
            return x * y;
        });
    }


    /**
     * 用自己的方式实现updateAndGet
     *
     * @param myUpdateAndSet
     */
    public static void myUpdateAndSet(MyUpdateAndSet myUpdateAndSet) {
        AtomicInteger in = new AtomicInteger(10);


        while (true) {
            int expected = in.get();
            int apply = myUpdateAndSet.apply(in.get());
            if (in.compareAndSet(expected, apply)) {
                break;
            }
        }
    }

    /**
     * 如果需要操作的不是基本数据类型，那么则使用AtomicReference，reference是引用，这个只能操作引用
     * 假设对象里面的属性发生改变了，这个是判断不出的
     */
    public static void atomicReferenceTest() {
        Account account1 = new Account(1000);
        Account account2 = new Account(100);
        AtomicReference<Account> ar = new AtomicReference<>(account1);
        while (true) {
            if (ar.compareAndSet(account1, account2)) {
                break;
            }
            log.debug("失败了一次");
        }
        log.debug("{}", ar.get().getMoney());
    }


    /**
     * A，B,A问题
     * AtomicReference解决不了该问题，一个线程目标是cas操作把对象从A改到B，实际上这个对象已经由另外的线程先从
     * A改到B，再由B改回A，这时由于AtomicReference只比较值，所有会把这次修改操作认为是成功的，这在大部分情况
     * 是没影响的，但是如果要记录状态变化之类的，这种肯定是不行的
     */
    public static void AToBToA() {
        Account account1 = new Account(1000);
        Account account2 = new Account(100);
        AtomicReference<Account> ar = new AtomicReference<>(account1);

        Thread t1 = new Thread(() -> {
            while (true) {
                ThreadSleep.sleep(2);
                if (ar.compareAndSet(account1, account2)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t1");


        Thread t2 = new Thread(() -> {
            while (true) {
                if (ar.compareAndSet(account1, account2)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t2");


        Thread t3 = new Thread(() -> {
            ThreadSleep.sleep(1);
            while (true) {
                if (ar.compareAndSet(account2, account1)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * 使用AtomicStampedReference解决A，B,A问题，AtomicStampedReference比AtomicReference多了个版本锁，完美解决
     */
    public static void AtomicStampTest() {
        Account account1 = new Account(1000);
        Account account2 = new Account(100);
        //AtomicReference<Account> ar = new AtomicReference<>(account1);
        AtomicStampedReference<Account> ar = new AtomicStampedReference<>(account1, 0);

        Thread t1 = new Thread(() -> {
            while (true) {
                int stamp = ar.getStamp();
                ThreadSleep.sleep(2);
                if (ar.compareAndSet(account1, account2, stamp, stamp + 1)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t1");


        Thread t2 = new Thread(() -> {
            while (true) {
                int stamp = ar.getStamp();
                if (ar.compareAndSet(account1, account2, stamp, stamp + 1)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t2");


        Thread t3 = new Thread(() -> {
            ThreadSleep.sleep(1);
            while (true) {
                int stamp = ar.getStamp();
                if (ar.compareAndSet(account2, account1, stamp, stamp + 1)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();
    }


    /**
     * 这里我再介绍一个api，AtomicMarkableReference，这个比起AtomicReference假如了一个布尔值用于标记是否发生更改的
     * 这只能用来降低ABA问题发生的几率，但不能解决
     */
    public static void AtomicMarkableTest() {
        Account account1 = new Account(1000);
        Account account2 = new Account(100);
        //AtomicReference<Account> ar = new AtomicReference<>(account1);
        AtomicMarkableReference<Account> ar = new AtomicMarkableReference<>(account1, false);
        Thread t1 = new Thread(() -> {
            while (true) {
                boolean marked = ar.isMarked();
                ThreadSleep.sleep(2);
                if (ar.compareAndSet(account1, account2, marked, !marked)) {
                    break;
                }
                log.debug("{}失败了一次", Thread.currentThread().getName());
            }
        }, "t1");
        t1.start();

    }

    /**
     * 原子数组
     */
    public static void atomicArrayTest() {
        List<Thread> list = new ArrayList<>();
        //   int[]  a = new int[10]; 线程不安全
        AtomicIntegerArray array = new AtomicIntegerArray(10);
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    int index = j % 10;
                    // a[index]++ 线程不安全
                    array.getAndIncrement(index);
                }
            });
            list.add(t);
        }
        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        for (int z = 0; z < 10; z++) {
            log.debug("{}长度{}", z, array.get(z));
        }
    }

    ;

    /**
     * 字段更新器
     */
    public static void atomicField() {
        AtomicReferenceFieldUpdater updater
                = AtomicReferenceFieldUpdater.newUpdater(Account.class, String.class, "name");
        Account account = new Account(100);
        account.setName("name");
        Object expected = updater.get(account);
        boolean b = updater.compareAndSet(account, expected, "456");
        log.debug("是否成功{}，值为{}", b, account.getName());

    }

    /**
     * LongAdder累加器
     */
    public static void longAddrTest() {
        long start = System.currentTimeMillis();
        LongAdder longAdder = new LongAdder();
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    longAdder.increment();
                }
            });
            list.add(t);
        }
        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        log.debug("adder耗时：{}", System.currentTimeMillis() - start);
    }

    /**
     * 跟longAdder进行效率对比
     */
    public static void longAddrCompareTest() {
        long start = System.currentTimeMillis();
        AtomicInteger integer = new AtomicInteger();
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    integer.getAndIncrement();
                }
            });
            list.add(t);
        }
        list.forEach(Thread::start);
        list.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        log.debug("atomicInt耗时：{}", System.currentTimeMillis() - start);
    }

    public static void unSafeTest() throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe safe = (Unsafe) theUnsafe.get(null);

        // 获取要修改的变量在内存中的偏移量
        long offset = safe.objectFieldOffset(Account.class.getDeclaredField("name"));
        Account account = new Account(1000);
        // 调用底层方法修改accout
        boolean b = safe.compareAndSwapObject(account, offset, null, "123");
        log.debug("偏移量{}，是否改成功{},改后的钱:{}", offset, b, account.getName());
    }

    public static void myAtomicTest() {
        MyAtomicInteger myAtomicInteger = new MyAtomicInteger();
        List<Thread> list  = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myAtomicInteger.getAndIncrease();
                }
            });
            list.add(t);
        }
        list.forEach(Thread::start);
        list.forEach(t-> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        log.debug("最终值:{}",myAtomicInteger.getValue());
    }
}
