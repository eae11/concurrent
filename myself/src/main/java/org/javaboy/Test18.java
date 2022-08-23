package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c.Test18")
public class Test18 {
    static final Object lock = new Object();
    static boolean flag = false;
    static ReentrantLock r = new ReentrantLock();
    static Condition c = r.newCondition();

    public static void main(String[] args) {
        m3();
    }

    public static void m1() {
        Thread t2 = new Thread(() -> {
            log.debug("2");
        }, "t2");

        Thread t1 = new Thread(() -> {
            try {
                t2.join();
                log.debug("1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "t1");
        t2.start();
        t1.start();
    }

    public static void m2() {
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("2");
                flag = true;
                c.signal();
            }

        }, "t2");

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!flag) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.debug("1");

        }, "t1");
        t2.start();
        t1.start();
    }

    public static void m3() {
        Thread t2 = new Thread(() -> {
            r.lock();
            try {
                log.debug("2");
                flag = true;
            } finally {
                r.unlock();
            }


        }, "t2");

        Thread t1 = new Thread(() -> {
            r.lock();
            try {
                while (!flag) {
                    try {
                        c.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("1");
            } finally {
                r.unlock();
            }


        }, "t1");
        t2.start();
        t1.start();

    }
}
