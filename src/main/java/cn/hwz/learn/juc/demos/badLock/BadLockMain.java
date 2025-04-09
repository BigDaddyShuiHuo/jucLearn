package cn.hwz.learn.juc.demos.badLock;

import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/15
 */
@Slf4j(topic = "c.BadLockMain")
public class BadLockMain {

    public static void main(String[] args) {
        //philosopherProblems();
        liveLock();
    }

    public static void philosopherProblems(){
        Chopstick c1 = new Chopstick("筷子1");
        Chopstick c2 = new Chopstick("筷子2");
        Chopstick c3 = new Chopstick("筷子3");
        Chopstick c4 = new Chopstick("筷子4");
        Chopstick c5 = new Chopstick("筷子5");

        Philosopher p1 = new Philosopher("哲学家1",c1,c2);
        Philosopher p2 = new Philosopher("哲学家2",c2,c3);
        Philosopher p3 = new Philosopher("哲学家3",c3,c4);
        Philosopher p4 = new Philosopher("哲学家4",c4,c5);
        Philosopher p5 = new Philosopher("哲学家5",c1,c5);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
    }

    static int count = 50;
    public static void liveLock(){
        Thread t1 = new Thread(()->{
            while (count>0){
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("count--");
                count--;
            }
        });

        Thread t2 = new Thread(()->{
            while (count<100){
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("count++");
                count++;
            }
        });
        t1.start();
        t2.start();
    }
}
