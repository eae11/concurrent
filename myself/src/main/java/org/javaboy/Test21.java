package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.Test21")
public class Test21 {
    static Thread t1;
    static Thread t2;
    static Thread t3;

    public static void main(String[] args) {
        ParkUnpark parkUnpark = new ParkUnpark(3);
        t1 = new Thread(() -> {
            parkUnpark.print("a", t2);
        });
        t2 = new Thread(() -> {
            parkUnpark.print("b", t3);
        });
        t3 = new Thread(() -> {
            parkUnpark.print("c", t1);
        });
        t1.start();
        t2.start();
        t3.start();
        //开始大家都停住,让t1开始
        LockSupport.unpark(t1);
    }
}

@Slf4j(topic = "c.ParkUnpark")
class ParkUnpark {
    private int loopNumber;

    public ParkUnpark(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String str, Thread next) {
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            log.debug(str);
            LockSupport.unpark(next);
        }
    }
}