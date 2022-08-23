package org.javaboy;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.Test33")
public class Test33 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        m2();
    }

    private static void m2() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        /*以前是子线程去干活,然后主线程要等待子线程的返回结果,主线程阻塞
        我感觉这种whenComplete说白了,子线程返回结果交给自己,自己去完成,不用主线程去拿结果了,主线程当然不阻塞了
        */
        try {
            CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                    int i = 10 / 0;
                    log.debug("执行任务");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "66";
                //第一个参数是结果,第二个参数是异常
            }, pool).whenComplete((v, e) -> {
                if (e == null) {
                    log.debug("任务执行结束,任务结果{}", v);
                }
            }).exceptionally((e) -> {
                e.printStackTrace();
                return null;
            });
        } finally {
            pool.shutdown();
        }

        //主线程不要立刻结束，否则CompletableFuture默认使用的线程池(守护线程)会立刻关闭:暂停3秒钟线程

        log.debug("主线程干别的事");
    }

    private static void m1() throws InterruptedException, ExecutionException {
        /*以前是子线程去干活,然后主线程要等待子线程的返回结果,主线程阻塞
        我感觉这种whenComplete说白了,子线程返回结果交给自己,自己去完成,不用主线程去拿结果了,主线程当然不阻塞了
        */
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                log.debug("执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "666";
            //第一个参数是结果,第二个参数是异常
        }).whenComplete((v, e) -> {
            if (e == null) {
                log.debug("任务执行结束,任务结果{}", v);
            }
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });

        //主线程不要立刻结束，否则CompletableFuture默认使用的线程池(守护线程)会立刻关闭:暂停3秒钟线程

        log.debug("主线程干别的事");
        Thread.sleep(3000);
    }
}
