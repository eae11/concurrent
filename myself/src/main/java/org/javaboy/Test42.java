package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.lang.Thread.sleep;
@Slf4j(topic = "c.Test42")
public class Test42 {
    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        }, "t2").start();
    }
}

// 自定义锁（不可重入锁）
class MyLock implements Lock {
    private MySync sync = new MySync();

    @Override // 加锁（不成功会进入等待队列）
    public void lock() {
        sync.acquire(1);
    }

    @Override // 加锁，可打断
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override  // 尝试加锁（一次）
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override // 尝试加锁，带超时
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override  // 解锁
    public void unlock() {
        //tryRelease只是吧onwer设置为null,state设置为0,不会去唤醒阻塞的线程
        //relaese会唤醒阻塞的线程
        sync.release(1);
    }

    @Override  // 创建条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }

    //独占锁  同步器类
    class MySync extends AbstractQueuedSynchronizer {
        @Override //独占锁,参数暂时用不到
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            //把setState放在setExclusiveOwnerThread后面
            //保证exclusiveOwnerThread也从主存中获取
            setState(0);
            //写屏障
            return true;
        }

        @Override // 是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }

    }
}