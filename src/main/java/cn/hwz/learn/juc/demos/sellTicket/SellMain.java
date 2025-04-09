package cn.hwz.learn.juc.demos.sellTicket;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * 买票练习
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/6
 */
@Slf4j(topic = "c.SellMain")
public class SellMain {

    public final static Integer WINDOWS_AMONUT = 100;
    public final static Integer TICKET_AMOUNT = 1000;
    public final static Random random = new Random();

    public static void main(String[] args) {
        Ticket ticket = new Ticket(TICKET_AMOUNT);
        // 多个线程共同操作list，如果简单用arrayList他会有线程安全问题
        List<Integer> sellList = new Vector<>();
        for (int i = 0;i<WINDOWS_AMONUT;i++){
            Thread thread = new Thread(()->{
                Integer sell = ticket.sell(randomTicket());
                sellList.add(sell);
            }
            );
            thread.start();
        }
        int sum = sellList.stream().mapToInt(i -> i).sum();
        int amount = ticket.getAmount();
        log.debug("总数：{}",sum+amount);
    }

    public static int randomTicket(){
        return random.nextInt(5)+1;
    }
}
