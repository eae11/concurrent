package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Test23")
public class Test23 {
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("启动...");
            try {
                //如果没有竞争那么此方法就会获取lock对象锁
                //如果有竞争就进入阻塞队列，可以被其它线程用interruput方法打断 也就是叫醒
                //lock.lock();//不可打断
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                log.debug("等锁的过程中被打断");
                return;
            }
            try {
                log.debug("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");
        //主线程先获得锁再启动t1线程
        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            sleep(1000);
            t1.interrupt();
            log.debug("执行打断");
        } finally {
            lock.unlock();
        }
    }
}
