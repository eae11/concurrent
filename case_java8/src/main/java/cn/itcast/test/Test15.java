package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test15")
public class Test15 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.debug("结束");
        }, "t1");
        //守护线程，只要其它非守护线程运行结束了，即使守护线程的代码没有执行完，也会强制结束。
        t1.setDaemon(true);
        t1.start();

        Thread.sleep(1000);
        log.debug("结束");
    }
}
