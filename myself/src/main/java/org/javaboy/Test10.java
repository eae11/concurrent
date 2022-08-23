package org.javaboy;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test10")
public class Test10 {
    public static void main(String[] args) {
        TwoPhaseTermination twoPhaseTermination = new TwoPhaseTermination();
        twoPhaseTermination.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        twoPhaseTermination.stop();
    }
}

@Slf4j(topic = "c.TwoPhaseTermination")
class TwoPhaseTermination {
    private Thread monitor;

    //启动监控线程
    public void start() {
        monitor = new Thread(() -> {

            while (true) {//监控线程在自己执行监控的过程中,去判断自己是否被打断
                Thread current = Thread.currentThread();//如果正在运行的线程被打断会设置标记为true,park的线程被打断也会设置打断标记为true
                //Thread.interrupted()也是判断打断标记,不同于isInterrupted方法,此方法是静态的,并且判断完了以后会把打断标记设置为false
                //if (Thread.interrupted()) {
                if (current.isInterrupted()) {
                    log.debug("监控线程关闭");
                    log.debug("{}", current.isInterrupted());
                    break;

                }
                try {
                    Thread.sleep(1000);  //如果被打断线程正在 sleep，wait，join 会导致被打断的线程抛出 InterruptedException,并且设置打断标记为false
                    log.debug("执行监控");
                } catch (InterruptedException e) {
                    //打断自己(也就是把打断标记设置为true)
                    current.interrupt();

                }

            }
        });
        monitor.start();
    }

    //停止监控线程
    public void stop() {
        //打断监控线程
        monitor.interrupt();
    }
}
