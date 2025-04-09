package cn.hwz.learn.juc.demos.orderOut;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/17
 */
@Slf4j(topic = "c.AlternateOutPark")
public class AlternateOutPark {
    public void print(Thread next,String str){
        while(true) {
            LockSupport.park();
            log.debug(str);
            LockSupport.unpark(next);
            ThreadSleep.sleep(1);
        }
    }
}
