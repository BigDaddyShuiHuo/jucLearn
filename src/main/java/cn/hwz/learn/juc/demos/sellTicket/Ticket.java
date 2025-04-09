package cn.hwz.learn.juc.demos.sellTicket;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/6
 */
public class Ticket {

    private Integer amount;

    public Ticket(Integer amount) {
        this.amount = amount;
    }

    // synchronized加到方法上，因为共享资源实际上是ticket，synchronized加到方法上等于
    // 锁是this，也就是该ticket对象，所以没问题
    public synchronized Integer sell(Integer count) {
        if (count <= amount) {
            amount = amount - count;
            return count;
        }
        return 0;
    }

    public Integer getAmount() {
        return amount;
    }
}

