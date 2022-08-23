package org.javaboy;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test11")
public class Test11 {

    public static void main(String[] args) throws InterruptedException {
       /* Thread t1 = new Thread(() -> {
            log.debug("sleep...");
            try {
                Thread.sleep(5000); //如果被打断线程正在 sleep，wait，join 会导致被打断的线程抛出 InterruptedException,并且设置打断标记为false
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");

        t1.start();
        Thread.sleep(1000);
        log.debug("interrupt");
        t1.interrupt();//打断t1线程
        log.debug("打断标记:{}", t1.isInterrupted());*/

        Thread t1 = new Thread(() -> {
            while (true) {
                ////如果正在运行的线程被打断会设置标记为true,
                //判断当前线程是否被打断了
                boolean interrupted = Thread.currentThread().isInterrupted();//Thread.interrupted()会判断打断标记然后将其设置为false
                if (interrupted) {
                    log.debug("被打断了, 退出循环");
                    break;
                }
                //Thread.currentThread().interrupt();//还可以自己把自己打断
            }
        }, "t1");
        t1.start();

        Thread.sleep(1000);
        log.debug("interrupt");
        t1.interrupt();
    }
}
