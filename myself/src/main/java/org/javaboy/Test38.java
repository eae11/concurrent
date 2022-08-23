package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/*
需求1： 5个销售卖房子，集团高层只关心销售总量的准确统计数。
需求2： 5个销售卖完随机数房子，各自独立销售额度，自己业绩按提成走，分灶吃饭，各个销售自己动手，丰衣足食
 */
@Slf4j(topic = "c.Test38")
public class Test38 {
    public static void main(String[] args) throws InterruptedException {
        House house = new House();
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                int size = new Random().nextInt(5) + 1;
                try {
                    for (int j = 1; j <= size; j++) {
                        house.saleHouse();
                        house.saleVolumeByThreadLocal();
                    }
                    log.debug((Thread.currentThread().getName() + "\t" + "号销售卖出：" + house.saleVolume.get()));

                } finally {
                    //一定得remove
                    house.saleVolume.remove();
                }
            }, String.valueOf(i)).start();
        }
        Thread.sleep(1000);
        log.debug("总共卖了多少{}", house.saleCount);
    }

}

class House //资源类
{
    int saleCount = 0;
   /* *//*ThreadLocal<Integer> saleVolume = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue()
        {
            return 0;
        }
    };*/
    ThreadLocal<Integer> saleVolume = ThreadLocal.withInitial(() -> 0);

    public synchronized void saleHouse() {
        ++saleCount;
    }

    public void saleVolumeByThreadLocal() {
        saleVolume.set(1 + saleVolume.get());
    }
}