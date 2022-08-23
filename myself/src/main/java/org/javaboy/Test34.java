package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j(topic = "c.Test34")
public class Test34 {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("开始执行任务");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "666";
        });
        //阻塞
        //log.debug("{}", f.join());
        Thread.sleep(1000);
    }

}
