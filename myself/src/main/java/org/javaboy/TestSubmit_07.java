package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j(topic = "c.TestSubmit_07")
public class TestSubmit_07 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Object> future = pool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                log.debug("running");
                Thread.sleep(1000);
                return "ok";
            }
        });
        //主线程等结果会阻塞
        log.debug("{}", future.get());

        log.debug("结束");
    }
}
