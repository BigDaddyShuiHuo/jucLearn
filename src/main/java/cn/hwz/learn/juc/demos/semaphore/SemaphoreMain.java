package cn.hwz.learn.juc.demos.semaphore;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**
 * @author needleHuo
 * @version jdk11
 * @description 信号量锁
 * @since 2025/4/3
 */
@Slf4j(topic = "c.SemaphoreMain")
public class SemaphoreMain {
    // 只有2个许可
    static Semaphore semaphore = new Semaphore(2);
    // 后面参数代表公平非公平，true为公平，不传默认非公平
    static Semaphore fairSemaphore = new Semaphore(3,true);

    public static void main(String[] args) {
        new Thread(()->{
            read();
        }).start();

        new Thread(()->{
            read();
        }).start();

        new Thread(()->{
            read();
        }).start();
    }

    public static void read(){
        try {
            semaphore.acquire();
            log.debug("reading.....{}",Thread.currentThread().getName());
            ThreadSleep.sleep(2);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            log.debug("release.....{}",Thread.currentThread().getName());
            semaphore.release();
        }
    }
}
