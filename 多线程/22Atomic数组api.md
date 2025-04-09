### Atomic数组类型api

atomic数组类是保证数组类型的原子性操作的，有下面三个类型

```java
AtomicIntegerArray
AtomicLongArray
AtomicReferenceArray
```

他们的api都是类似的，以下以AtomicIntegerArray为例去介绍api

---

现在有一个情况

我需要把一个长度为10的数组，用10个线程对他里面每个元素进行累加，使得加起来总和为100，000。

10个线程，每个线程负责自增一万

```java
public static void atomicArrayTest(){
    int[]  a = new int[10];
    AtomicIntegerArray array = new AtomicIntegerArray(10);
    for (int i =0;i<10;i++){
        Thread t = new Thread(()->{
            for (int j=0;j<10000;j++){
                int index = j%10;
                a[index]++;
            }
        });
        list.add(t);
    }
    list.forEach(Thread::start);
    list.forEach(t->{
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });
    for (int z = 0;z<10;z++){
        log.debug("{}长度{}",a[z]);
    }
};
```

上面代码肯定会有线程问题的，所以，我用原子数组进行替换

```java
public static void atomicArrayTest(){
    List<Thread> list = new ArrayList<>();
 //   int[]  a = new int[10];
    AtomicIntegerArray array = new AtomicIntegerArray(10);
    for (int i =0;i<10;i++){
        Thread t = new Thread(()->{
            for (int j=0;j<10000;j++){
                int index = j%10;
                array.getAndIncrement(index);
            }
        });
        list.add(t);
    }
    list.forEach(Thread::start);
    list.forEach(t->{
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });
    for (int z = 0;z<10;z++){
        log.debug("{}长度{}",z,array.get(z));
    }
};
```

关键点

 array.getAndIncrement(index);