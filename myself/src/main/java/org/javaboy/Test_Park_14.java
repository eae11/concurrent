package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.Test_Park_14")
public class Test_Park_14 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park");
            LockSupport.park();//park的时候被打断,会继续执行然后把打断标记设置为true,如果打断标记为true了,再次park也没用,
            // 只有把打断标记设置为false才能park (用Thread.interrupted(),判断打断标记然后将其设置为false)
            log.debug("unpark");
            log.debug("{}", Thread.interrupted());
            log.debug("aaa");
            //LockSupport.park();

        }, "t1");
        t1.start();

        Thread.sleep(1000);
        t1.interrupt();
    }
}
