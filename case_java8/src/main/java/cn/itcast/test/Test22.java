package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

import static cn.itcast.n2.util.Sleeper.sleep;

@Slf4j(topic = "c.Test22")
public class Test22 {
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("启动...");
            try {
                //如果没有竞争那么此方法就会获取lock对象锁
                //如果有竞争就进入阻塞队列，可以被其它线程用interruput方法打断
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("等锁的过程中被打断");
                return;
            }
            try {
                log.debug("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");
        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            sleep(1);
            t1.interrupt();
            log.debug("执行打断");
        } finally {
            lock.unlock();
        }

        /*Thread t1 = new Thread(() -> {
            log.debug("尝试获得锁");
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.debug("获取不到锁");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        sleep(1);
        log.debug("释放了锁");
        lock.unlock();*/

       /* try {
            lock.lock();
            log.debug("main");
            m1();
        } finally {
            //lock.unlock();
        }*/


    }

    public static void m1() {
        try {
            lock.lock();
            log.debug("m1");
            m2();
        } finally {
            //lock.unlock();
        }

    }

    public static void m2() {
        try {
            lock.lock();
            log.debug("m2");
        } finally {
            lock.unlock();
        }
    }
}
