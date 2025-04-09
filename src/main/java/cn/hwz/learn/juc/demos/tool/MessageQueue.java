package cn.hwz.learn.juc.demos.tool;

import cn.hwz.learn.juc.demos.test.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author needleHuo
 * @version jdk11
 * @description 设计模式-多线程通信-消息队列
 * @since 2025/3/14
 */
@Slf4j(topic = "c.MessageQueue")
public final class MessageQueue {
    private final LinkedList<Message> messageList = new LinkedList<>();
    // 最大3条消息
    private final int cap = 2;

    /**
     * 生产者
     * 上限为3，大于三则等待
     * @param message
     */
    public void put(Message message) throws InterruptedException {
        synchronized (messageList){
            while (messageList.size()>=cap){
                log.debug("任务数量大于3，暂时挂起");
                messageList.wait();
            }
            messageList.add(message);
            // 加进去证明有消息消费了，可以唤醒消费者了
            messageList.notifyAll();
        }
    }

    public Message take() throws InterruptedException {
        synchronized (messageList){
            while (messageList.isEmpty()){
                messageList.wait();
            }
            Message remove = messageList.remove();
            // 已经消费消息了，消息队列为空了，可以继续放了
            messageList.notifyAll();
            return remove;
        }
    }

}

