package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.Test37")
public class Test37 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        m1();
    }

    //没有传入自定义线程池，都用默认线程池ForkJoinPool;(默认的是守护线程,主线程结束,全部强制结束)
    //如果传入了自定义线程池,
    //thenRun方法跟随上一个任务使用的线程池
    //thenRunAsync方法使用ForkJoinPool线程池
    //3备注:有可能处理太快，系统优化切换原则，直接使用main线程处理
    //其它如: thenAccept和thenAcceptAsync，thenApply和thenApplyAsync等，它们之间的区别也是同理
    private static void m1() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        CompletableFuture<Void> f;
        try {
            f = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("第一个任务");
                return "666";//这个使用使用自定义线程池
            }, pool).thenRun(() -> {//跟随上一个任务使用自定义线程池
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("第二个任务");

            }).thenRunAsync(() -> {//使用ForkJoinPool线程池
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("第三个任务");
            }).thenRun(() -> {//跟随上一个任务使用的线程池
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("第四个任务");
            });
        } finally {
            pool.shutdown();
        }
        log.debug("{}", f.get());
    }
}
