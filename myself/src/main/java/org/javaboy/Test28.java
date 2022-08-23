package org.javaboy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.Test28")
public class Test28 {
    public static void main(String[] args) {
        method2();
    }
    //延迟一秒,每隔一秒执行一次
    private static void method3() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");
        pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
        }, 1, 1, TimeUnit.SECONDS);
    }

    //使用 ScheduledExecutorService 改写
    private static void method2() {
        //ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.schedule(() -> {
            log.debug("task1");
            int i = 1 / 0;//出异常也不会影响另一个任务
            /*try {
                sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }, 1, TimeUnit.SECONDS);

        pool.schedule(() -> {
            log.debug("task2");
        }, 1, TimeUnit.SECONDS);
    }

    private static void method1() {
        /*Timer 的优点在于简单易用，但
由于所有任务都是由同一个线程来调度，因此所有任务都是串行执行的，同一时间只能有一个任务在执行，前一个
任务的延迟或异常都将会影响到之后的任务。
*/
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                log.debug("task 1");
                sleep(2);
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        log.debug("start...");
        // 使用 timer 添加两个任务，希望它们都在 1s 后执行
        // 但由于 timer 内只有一个线程来顺序执行队列中的任务，因此『任务1』的延时，影响了『任务2』的执行
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);
    }
}
