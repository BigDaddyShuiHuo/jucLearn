package cn.hwz.learn.juc.demos.test;

import cn.hwz.learn.juc.demos.tool.MessageQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/14
 */
@Slf4j(topic = "c.MessageQueueTest")
public class MessageQueueTest {
    public static void main(String[] args) throws InterruptedException {

        // 生产者
        MessageQueue mq = new MessageQueue();
        for (int i=0;i<3;i++){
            int j = i;
            Thread t = new Thread(()->{
                Message m = new Message(j);
                try {
                    mq.put(m);
                    log.debug("生产消息:{}",m.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            },"生产者"+j);
            t.start();
        }
        Thread.sleep(2000);
        // 消费者
        for (int i=0;i<3;i++){
            Thread.sleep(1000);
            int j = i;
            Thread t = new Thread(()->{
                try {
                    Message take = mq.take();
                    log.debug("消费消息:{}",take.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            },"生产者"+j);
            t.start();
        }
    }
}
