package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Test44")
public class Test44 {
    public static void main(String[] args) throws InterruptedException {
        m2();
    }

    private static void m2() throws InterruptedException {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        new Thread(() -> {
            try {
                dataContainer.read(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        sleep(500);
        new Thread(() -> {
            try {
                dataContainer.write(0);//写入新数据
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }

    private static void m1() throws InterruptedException {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        new Thread(() -> {
            try {
                dataContainer.read(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        sleep(500);
        new Thread(() -> {
            try {
                dataContainer.read(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}

@Slf4j(topic = "c.DataContainerStamped")
class DataContainerStamped {
    private final StampedLock lock = new StampedLock();
    private int data;

    public DataContainerStamped(int data) {
        this.data = data;
    }

    public int read(int readTime) throws InterruptedException {
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        sleep(readTime);//模拟读取耗费的时间
        // 验戳 如果通过表示这期间确实没有写操作，数据可以安全使用，
        // 如果校验没通过，需要重新获取读锁，保证数据安全。
        if (lock.validate(stamp)) {
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }
        // 锁升级 - 读锁
        log.debug("updating to read lock... {}", stamp);
        try {
            stamp = lock.readLock();
            log.debug("read lock {}", stamp);
            sleep(readTime);
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        } finally {
            log.debug("read unlock {}", stamp);
            lock.unlockRead(stamp);
        }
    }

    public void write(int newData) throws InterruptedException {
        long stamp = lock.writeLock();
        log.debug("write lock {}", stamp);
        try {
            sleep(2000);
            this.data = newData;
        } finally {
            log.debug("write unlock {}", stamp);
            lock.unlockWrite(stamp);
        }
    }


}
