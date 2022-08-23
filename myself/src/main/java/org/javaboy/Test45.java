package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Test45")
public class Test45 {
    public static void main(String[] args) {
        // 1. 创建 semaphore 对象
        Semaphore semaphore = new Semaphore(3);//最多几个获取锁
        // 2. 10个线程同时运行
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();//获取锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running...");
                    sleep(1000);
                    log.debug("end...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();//释放锁
                }
            }).start();
        }
    }
}

