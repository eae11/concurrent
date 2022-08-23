package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j(topic = "c.TestShutDown_09")
public class TestShutDown_09 {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<Integer> result1 = pool.submit(() -> {
            log.debug("task 1 running...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.debug("被打断");
                e.printStackTrace();
            }
            log.debug("task 1 finish...");
            return 1;
        });

        Future<Integer> result2 = pool.submit(() -> {
            log.debug("task 2 running...");
            //try {
                Thread.sleep(1000);
            //} catch (InterruptedException e) {
            //    log.debug("被打断");
            //    e.printStackTrace();
            //}
            log.debug("task 2 finish...");
            return 2;
        });

        Future<Integer> result3 = pool.submit(() -> {
            log.debug("task 3 running...");
            Thread.sleep(1000);
            log.debug("task 3 finish...");
            return 3;
        });

        log.debug("shutdown");
/*线程池状态变为 SHUTDOWN
- 不会接收新任务
- 但已提交任务会执行完
- 此方法不会阻塞调用线程的执行
*/
        //pool.shutdown();//不会影响正在执行的任务,也不会影响在阻塞队列的任务执行 主线程也不会阻塞
        //pool.awaitTermination(3, TimeUnit.SECONDS);//可以让主线程主动等待(阻塞),要在shutdown之后使用
/*
线程池状态变为 STOP
- 不会接收新任务
- 会将队列中的任务返回
- 并用 interrupt 的方式中断正在执行的任务
*/
        //返回队列里的任务,正在执行的任务打断
        List<Runnable> runnables = pool.shutdownNow();
        log.debug("other.... {}", runnables);
    }
}
