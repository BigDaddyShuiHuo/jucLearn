package cn.hwz.learn.juc.demos.atmoicTest;

import cn.hwz.learn.juc.demos.transfer.Account;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/20
 */
@Slf4j(topic = "c.AtomicAccount")
public class AtomicAccount {
    public AtomicInteger money;

    public AtomicAccount(int money) {
        this.money = new AtomicInteger(money);
    }


    public void transfer(int transferM) {
        while (true) {  // 这里就是不断进行cas操作，直到成功为止
            int original = money.get();  // 进去看源码发现AtomicInteger的value是又volatile修饰的
            int result = original - transferM;
            if (money.compareAndSet(original,result)){  // cas操作，成功返回true
                break;
            }

        }
        // 改写
        //money.getAndAdd(-1*transferM);
    }

    public Integer getMoney() {
        return money.get();
    }
}
