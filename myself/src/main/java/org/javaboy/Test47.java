package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static org.javaboy.Sleeper.sleep;


@Slf4j(topic = "c.Test47")
public class Test47 {
    public static void main(String[] args) throws InterruptedException {
        //ExecutorService service = Executors.newFixedThreadPool(2);
        ExecutorService service = Executors.newFixedThreadPool(3);
        CyclicBarrier barrier = new CyclicBarrier(2, () -> {
            //count为0时汇总执行
            log.debug("task1, task2 finish...");
        });
        for (int i = 0; i < 3; i++) {
            service.submit(() -> {
                log.debug("task1 begin...");
                sleep(1);
                try {
                    barrier.await(); // 2-1=1 为0就往下运行了
                    log.debug("task1 end...");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            service.submit(() -> {
                log.debug("task2 begin...");
                sleep(2);
                try {
                    barrier.await(); // 1-1=0
                    log.debug("task2 end...");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
    }

    private static void test1() {
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 3; i++) {
            CountDownLatch latch = new CountDownLatch(2);
            service.submit(() -> {
                log.debug("task1 start...");
                sleep(1);
                latch.countDown();
            });
            service.submit(() -> {
                log.debug("task2 start...");
                sleep(2);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task1 task2 finish...");
        }
        service.shutdown();
    }
}

