package cn.hwz.learn.juc.demos.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author needleHuo
 * @version jdk11
 * @description 线程池中的饥饿
 * @since 2025/3/26
 */
@Slf4j(topic = "c.TestNotEnough")
public class TestNotEnough {
    static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");
    static Random RANDOM = new Random();

    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = executorService.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });


//        executorService.execute(() -> {
//            log.debug("处理点餐...");
//            Future<String> f = executorService.submit(() -> {
//                log.debug("做菜");
//                return cooking();
//            });
//            try {
//                log.debug("上菜: {}", f.get());
//            } catch (InterruptedException | ExecutionException e) {
//
//                e.printStackTrace();
//            }
//        });
    }
}
