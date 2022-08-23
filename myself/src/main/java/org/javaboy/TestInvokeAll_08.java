package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.TestInvokeAll_08")
public class TestInvokeAll_08 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

     /*   List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.debug("begin");
                    Thread.sleep(1000);
                    return "1";
                },
                () -> {
                    log.debug("begin");
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    log.debug("begin");
                    Thread.sleep(2000);
                    return "3";
                }
        ));
        //主线程阻塞,等待所有任务执行完了才继续
        for (Future<String> future : futures) {
            log.debug("{}", future.get());
        }*/

        //这三个任务中谁先执行完就返回谁
        Object o = pool.invokeAny(Arrays.asList(
                () -> {
                    log.debug("begin");
                    Thread.sleep(1000);
                    return "1";
                },
                () -> {
                    log.debug("begin");
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    log.debug("begin");
                    Thread.sleep(2000);
                    return "3";
                }
        ));
        //主线程阻塞,等待所有任务执行完了才继续
        log.debug("{}", o);
    }
}
