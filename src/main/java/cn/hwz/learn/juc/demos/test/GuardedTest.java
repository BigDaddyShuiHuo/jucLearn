package cn.hwz.learn.juc.demos.test;

import cn.hwz.learn.juc.demos.tool.GuardedObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author needleHuo
 * @version jdk11
 * @description 测试保护性等待
 * @since 2025/3/12
 */
@Slf4j(topic = "c.GuardedTest")
public class GuardedTest {
    public static void main(String[] args) {
        test1();
    }

    public static void test1(){
        GuardedObj guardedObj = new GuardedObj();
        Thread t1 = new Thread(()->{
            try {
                log.debug("t1工作");
                Object o = guardedObj.get(3000);
                log.debug(o+"");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        },"t1");
        t1.start();
        Thread t2 = new Thread(()->{
            try {
                log.debug("t2工作");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            guardedObj.complete(new Object());
        },"t2");

        t2.start();
    }
}
