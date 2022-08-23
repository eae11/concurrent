package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c.Test22")
public class Test22 {
    static ReentrantLock lock = new ReentrantLock();

    /*
    可重入是指同一个线程如果首次获得了这把锁，那么因为它是这把锁的拥有者，因此有权利再次获取这把锁
    如果是不可重入锁，那么第二次获得锁时，自己也会被锁挡住
    */
    public static void main(String[] args) {
        method1();
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("子线程执行");
                method2();
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public static void method1() {
        lock.lock();
        try {
            log.debug("execute method1");
            method2();
        } finally {
            lock.unlock();//解锁需要多次state要减为0,否则别人线程来获取锁阻塞
        }
    }

    public static void method2() {
        lock.lock();
        try {
            log.debug("execute method2");
            method3();
        } finally {
            lock.unlock();
        }
    }

    public static void method3() {
        lock.lock();
        try {
            log.debug("execute method3");
        } finally {
            lock.unlock();
        }
    }

}
