package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.javaboy.Sleeper.sleep;


@Slf4j(topic = "c.Test46")
public class Test46 {
    public static void main(String[] args) throws InterruptedException {
        test2();
    }

    private static void test5() {
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(() -> {
            log.debug("begin...");
            sleep(1);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        });
        service.submit(() -> {
            log.debug("begin...");
            sleep(1.5);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        });
        service.submit(() -> {
            log.debug("begin...");
            sleep(2);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        });
        service.submit(() -> {
            try {
                log.debug("waiting...");
                latch.await();
                log.debug("wait end...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        service.shutdown();
    }

    private static void test4() throws InterruptedException {
        //对比join join比较繁琐,而且一般不会直接创建线程,而是使用线程池,核心线程不会结束,没办法使用join
        CountDownLatch latch = new CountDownLatch(3);//state设置为3

        new Thread(() -> {
            log.debug("begin...");
            sleep(1);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        }).start();

        new Thread(() -> {
            log.debug("begin...");
            sleep(2);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        }).start();

        new Thread(() -> {
            log.debug("begin...");
            sleep(1.5);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        }).start();

        log.debug("waiting...");

        latch.await();//当countDown把state减为0主线程恢复
        log.debug("wait end...");
    }

    private static void test2() throws InterruptedException {
        AtomicInteger num = new AtomicInteger(0);
        ExecutorService pool = Executors.newFixedThreadPool(10, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "t" + num.getAndIncrement());
            }
        });
        CountDownLatch latch = new CountDownLatch(10);
        String[] all = new String[10];
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < 10; i++) {
            int x = i;
            pool.submit(() -> {
                for (int j = 0; j <= 100; j++) {
                    try {
                        Thread.sleep(r.nextInt(100));//模拟延迟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    all[x] = Thread.currentThread().getName() + "(" + j + ")" + "%";
                    //后面打印覆盖前面
                    System.out.print("\r" + Arrays.toString(all));
                }
                latch.countDown();
            });
        }

        latch.await();
        System.out.println("\n游戏开始");
        pool.shutdown();
    }
}

