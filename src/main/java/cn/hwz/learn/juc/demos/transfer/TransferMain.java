package cn.hwz.learn.juc.demos.transfer;

import cn.hwz.learn.juc.demos.sellTicket.Ticket;
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
@Slf4j(topic = "c.TransferMain")
public class TransferMain {

    public final static Integer TRANSFER_AMONUT = 100;

    public final static Random random = new Random();

    public static void main(String[] args) {
        Account a = new Account(1000);
        Account b = new Account(1000);

        for (int i = 0;i<TRANSFER_AMONUT;i++){
            Thread t1 = new Thread(()->{
                a.transfer(b,randomTicket());
            });
            Thread t2 = new Thread(()->{
                b.transfer(a,randomTicket());
            });
            t1.start();
            t2.start();
        }

        log.debug("总数：{}",a.getMoney()+b.getMoney());
    }

    public static int randomTicket(){
        return random.nextInt(100)+1;
    }
}
