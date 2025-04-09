package cn.hwz.learn.juc.demos.blockList;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/4/9
 */
@Slf4j(topic = "c.BlockListMain")
public class BlockListMain {
    public static void main(String[] args) {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();
        ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<>();
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        CopyOnWriteArraySet copyOnWriteArraySet = new CopyOnWriteArraySet();
    }
}
