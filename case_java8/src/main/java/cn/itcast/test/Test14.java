package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static cn.itcast.n2.util.Sleeper.sleep;

@Slf4j(topic = "c.Test14")
public class Test14 {

    private static void test4() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                log.debug("park...");
                LockSupport.park();
                log.debug("打断状态：{}", Thread.interrupted());//判断打断标记,并且会把打断标记重置为假
            }
        });
        t1.start();


        sleep(1);
        t1.interrupt();
    }

    private static void test3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            log.debug("打断状态：{}", Thread.currentThread().isInterrupted());//判断当前的线程的打断状态
            LockSupport.park();// t1.interrupt(); 会将t1线程从阻塞打断变可运行状态,并设置打断标记为true,如果打断标记已经为true了此时打断就没用了
            log.debug("unpark...");
            log.debug("打断状态：{}", Thread.currentThread().isInterrupted());
        }, "t1");
        t1.start();

        sleep(2);
        t1.interrupt();

    }

    public static void main(String[] args) throws InterruptedException {
        test4();
    }
}
