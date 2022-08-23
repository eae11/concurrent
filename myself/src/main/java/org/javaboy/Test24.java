package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Test24")
public class Test24 {
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("尝试获得锁");
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {//2秒内尝试获取锁
                    log.debug("获取不到锁");
                    return;
                }
            } catch (InterruptedException e) {
                log.debug("获取不到锁");
                return;
            }
            try {
                log.debug("获得到锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        log.debug("获得到锁");
        t1.start();
        sleep(3000);
        log.debug("释放了锁");
        lock.unlock();
    }
}
