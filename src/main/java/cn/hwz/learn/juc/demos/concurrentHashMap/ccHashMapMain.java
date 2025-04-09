package cn.hwz.learn.juc.demos.concurrentHashMap;

import cn.hwz.learn.juc.demos.tool.ThreadSleep;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/4/5
 */
@Slf4j(topic = "c.ccHashMapMain")
public class ccHashMapMain {
    public static void main(String[] args) throws InterruptedException {
      //  for (int i = 0;i<50;i++) {
            //unsafeSum();
           // safeSum();
        //}
        testMethod();


    }

    /**
     * 模拟hashmap的线程不安全
     * 3个线程去统计3个单词中a的数量，并放到一个hashMap中，map的key为字符串a，value为数量
     */
    public static void unsafeSum() throws InterruptedException {
        Map<String,Integer> map = new HashMap<>();
        BiConsumer<Character,Map<Character,Integer>> compute = new BiConsumer<Character, Map<Character, Integer>>() {
            @Override
            public void accept(Character character, Map<Character, Integer> map) {
                if ('a'==character){
                    Integer i = map.get(character);
                    if (i==null){
                        i = 0;
                    }
                    i++;
                    map.put(character,i);
                }
            }
        };

        Consumer<Map<Character,Integer>> sum = new Consumer<Map<Character, Integer>>() {
            @Override
            public void accept(Map<Character, Integer> map) {
                log.debug("次数：{}",map.get('a'));
            }
        };
        sumCharacterk(map,compute,sum);
    }

    /**
     * 使用concurrentHashMap进行安全计算
     * ConcurrentHashMap里面的方法是能够保证原子性的
     */
    public static void safeSum() throws InterruptedException {
        ConcurrentHashMap<Character, LongAdder> cMap = new ConcurrentHashMap<>();
        BiConsumer<Character,ConcurrentHashMap<Character, LongAdder>> compute
                = new BiConsumer<Character, ConcurrentHashMap<Character, LongAdder>>() {
            @Override
            public void accept(Character c, ConcurrentHashMap<Character, LongAdder> cMap) {
                // 这个值如果有，就返回这个值，没有就执行lambda表达式
                LongAdder longAdder = cMap.computeIfAbsent('a', key -> new LongAdder());
                // computeIfAbsent能保证原子性，increment也能保证原子性，所以线程安全,if只是一个判断，不涉及共享变量的修改
                // 与阅读，所以if没有线程安全问题
                if (c=='a') {
                    longAdder.increment();
                }
            }
        };

        Consumer<ConcurrentHashMap<Character,LongAdder>> sum
                = new Consumer<ConcurrentHashMap<Character, LongAdder>>() {
            @Override
            public void accept(ConcurrentHashMap<Character, LongAdder> cMap) {
                LongAdder longAdder = cMap.get('a');
                log.debug("总数:{}",longAdder.sum());
            }
        };
        sumCharacterk(cMap,compute,sum);
    }
    /**
     * 统计单纯数量
     * @param map
     * @param compute
     * @param sum
     */
    public static void sumCharacterk(Map map, BiConsumer compute, Consumer sum) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(3);
        String word1 = "abcdabcdabcdabcdabcdabcdabcdabcd";
        String word2 = "abcdabcdabcdabcdabcdabcdabcdabcd";
        String word3 = "abcdabcdabcdabcdabcdabcdabcdabcd";

        new Thread(()->{
            for (int i =0;i<word1.length();i++){
                // 接受word1的元素，放到map中
                compute.accept(word1.charAt(i),map);
            }
            countDownLatch.countDown();
        }).start();

        new Thread(()->{
            for (int i =0;i<word2.length();i++){
                compute.accept(word2.charAt(i),map);
            }
            countDownLatch.countDown();
        }).start();

        new Thread(()->{
            for (int i =0;i<word3.length();i++){
                compute.accept(word3.charAt(i),map);
            }
            countDownLatch.countDown();
        }).start();

        // 正常来说要join或者CountDownLatch的，我懒得高了，直接sleep
        countDownLatch.await();
        sum.accept(map);

    }

    public static void testMethod() {
        log.debug("{}",1 << 2);
    }
}
