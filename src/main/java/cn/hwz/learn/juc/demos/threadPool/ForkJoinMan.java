package cn.hwz.learn.juc.demos.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/28
 */
@Slf4j(topic = "c.ForkJoinMan")
public class ForkJoinMan {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        Integer invoke = pool.invoke(new MyTask(1, 5));
        log.debug("{}",invoke);
    }

}

@Slf4j(topic = "c.MyTask")
class MyTask extends RecursiveTask<Integer> {

    int begin;
    int end;

    public MyTask(int begin,int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {

        log.debug("start:{},end:{}",begin,end);
        // 递归出口，执行递归
        if (begin==end){
            return begin;
        }

        int mid = (begin+end)/2;
        MyTask task1 = new MyTask(begin,mid);
        MyTask task2 = new MyTask(mid+1,end);
        // 让一个线程去执行task1
        task1.fork();
        // 让令一个线程执行task2
        task2.fork();

        int result = task1.join()+task2.join();
        return result;
    }
}
