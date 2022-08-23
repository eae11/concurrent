package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Test_Park_13")
public class Test_Park_13 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        }, "t1");
        t1.start();

        sleep(2000);
        log.debug("unpark...");
        LockSupport.unpark(t1);//如果先unpark,在park是的时候是不会阻塞的
    }
}
