package cn.hwz.learn.juc.demos.orderOut;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description 交替输出，3个线程按照abc方式进行输出，本质上也是交替输出
 * @since 2025/3/17
 */
@Slf4j(topic = "c.AlternateOut")
public class AlternateOutWait {

    int flag;

    public AlternateOutWait(int flag) {
        this.flag = flag;
    }

    public void  print(int current, int next, String str){
        synchronized (this){
            while (true) {
                while (flag != current) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug(str);
                flag = next;
                ThreadSleep.sleep(1);
                this.notifyAll();
            }
        }
    }

}
