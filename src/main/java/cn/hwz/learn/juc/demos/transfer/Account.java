package cn.hwz.learn.juc.demos.transfer;

/**
 * @author needleHuo
 * @version jdk11
 * @description
 * @since 2025/3/6
 */
public class Account {

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;

    private Integer money;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public   volatile String name;

    public Account(Integer money) {
        this.money = money;
    }

    /**
     * 注意了，这里在方法上加synchronized是没有用的，因为在方法上加synchronized
     * 他的锁是this，如果是a向b转账，那么，锁是a。如果b向a转，锁就是b，不是同一个锁，没有办法区保证
     * a和b的总数是一样的，所以这里用了Account.class
     * @param target
     * @param money
     * @return
     */
    public Integer transfer(Account target, Integer money) {
        synchronized (Account.class) {
            if (money <= this.money) {
                reduceMoney(money);
                target.addMoney(money);
            }
            return 0;
        }
    }

    public void addMoney(Integer money) {
        this.money = this.money + money;
    }

    public void reduceMoney(Integer money) {
        this.money = this.money - money;
    }

    public Integer getMoney() {
        return money;
    }
}

