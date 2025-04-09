package cn.hwz.learn.juc.demos.test;

import cn.hwz.learn.juc.demos.tool.GuardedBox;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/14
 */
@Slf4j(topic = "c.GuardedBoxTest")
public class GuardedBoxTest {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> ids = new ArrayList<>();
        for (int i=0;i<5;i++){
            Person person = new Person();
            ids.add(person.getGuardedObjId());
            person.start();
        }

       Thread.sleep(2000);

        for (Integer i :ids){
            PostMan postMan = new PostMan(i);
            postMan.start();
        }

    }
}

@Slf4j(topic = "c.Person")
class Person extends Thread {

    public Person() {
        this.guardedObjId = GuardedBox.createGuardedObj();
    }

    private int guardedObjId;

    @Override
    public void run() {
        log.debug("开始等待任务:{}",guardedObjId);
        try {
            Object object = GuardedBox.getObject(guardedObjId, 5000);
            log.debug("当前id为:{},已完成任务:{}", guardedObjId, object);
        } catch (InterruptedException e) {
            log.debug("等待任务时发生异常:{}", guardedObjId);
            throw new RuntimeException(e);
        }
    }

    public int getGuardedObjId() {
        return guardedObjId;
    }
}

@Slf4j(topic = "c.PostMan")
class PostMan extends Thread{

    public PostMan(int guardedObjId) {
        this.guardedObjId = guardedObjId;
    }

    private int guardedObjId;

    @Override
    public void run() {
        try {
            Object result = new Object();
            log.debug("正在进行:{}", guardedObjId);
            GuardedBox.completeGuard(guardedObjId, result);
            log.debug("已完成:{}", guardedObjId);
        }catch (Exception e){
            log.debug("完成任务时发生异常:{}", guardedObjId);
            throw e;
        }
    }

}