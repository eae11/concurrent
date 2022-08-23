package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Tset43")
public class Tset43 {

    public static void main(String[] args) throws InterruptedException {
        DataContainer dataContainer = new DataContainer();
        //m2(dataContainer);
        m1(dataContainer);

    }


    /*
    注意事项
读锁不支持条件变量
重入时升级不支持:即持有读锁的情况下去获取写锁，会导致获取写锁永久等待
重入时降级支持：即持有写锁的情况下去获取读锁
    */
    private static void m2(DataContainer dataContainer) {//读写互斥
        new Thread(() -> {
            try {
                dataContainer.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                dataContainer.write();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }

    private static void m1(DataContainer dataContainer) {//读读不互斥
        new Thread(() -> {
            try {
                dataContainer.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                dataContainer.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }


}


@Slf4j(topic = "c.DataContainer")
class DataContainer {
    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public void write() throws InterruptedException {
        log.debug("获取写锁...");
        w.lock();
        try {
            log.debug("写入");
            sleep(1000);
        } finally {
            log.debug("释放写锁...");
            w.unlock();
        }
    }

    public Object read() throws InterruptedException {
        log.debug("获取读锁...");
        r.lock();
        try {
            log.debug("读取");
            sleep(1000);
            return data;
        } finally {
            log.debug("释放读锁...");
            r.unlock();
        }
    }
}