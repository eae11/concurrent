package org.javaboy;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test12")
public class Test12 {
    public static void main(String[] args) {
        TwoPhaseTermination2 twoPhaseTermination2 = new TwoPhaseTermination2();
        twoPhaseTermination2.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        twoPhaseTermination2.stop();
    }
}

@Slf4j(topic = "c.TwoPhaseTermination2")
class TwoPhaseTermination2 {
    private Thread monitor;
    private volatile boolean stop=false;

    //启动监控线程
    public void start() {
        monitor = new Thread(() -> {

            while (true) {
                if (stop) {
                    log.debug("监控线程关闭");
                    break;

                }
                try {
                    Thread.sleep(1000);  //如果被打断线程正在 sleep，wait，join 会导致被打断的线程抛出 InterruptedException,并且设置打断标记为false
                    log.debug("执行监控");
                } catch (InterruptedException e) {
                }

            }
        });
        monitor.start();
    }

    //停止监控线程
    public void stop() {
        stop = true;
        monitor.interrupt();//如果线程在sleep还要等到sleep结束下次循环的时候才能把才能退出,加个这个让他快点退出
    }
}
