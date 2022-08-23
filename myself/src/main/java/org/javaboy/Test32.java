package org.javaboy;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.Test32")
public class Test32 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        m4();
    }

    private static void m4() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                log.debug("执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "我有返回值";
        }, pool);
        //阻塞
        log.debug("{}", f.get());
        pool.shutdown();

    }

    private static void m3() throws InterruptedException, ExecutionException {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                log.debug("执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "我有返回值";
        });
        //阻塞
        log.debug("{}", f.get());

    }

    private static void m2() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("执行任务");
        }, pool);//创建的线程交给线程池管理
        //futuretask能干的它也能干  阻塞
        log.debug("{}", f.get());
        log.debug("hhh");
        pool.shutdown();
    }

    //无返回值
    private static void m1() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("执行任务");
        });
        //futuretask能干的它也能干  阻塞
        log.debug("{}", f.get());
    }
}
