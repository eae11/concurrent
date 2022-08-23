package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic = "c.Test31")
public class Test31 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> f = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2000);
                log.debug("我是callable");
                return "666";
            }
        });
        Thread t1 = new Thread(f, "t1");
        t1.start();
        //主线程阻塞
        //log.debug(f.get());

      /*  try {
            log.debug(f.get(1, TimeUnit.SECONDS));//等一秒钟,一秒钟后没收到抛异常
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/

        while (true) {
            Thread.sleep(1000);
            if (f.isDone()) {//轮询去问,看有没有结果 (鸡肋)
                log.debug(f.get());
                break;
            } else {
                log.debug("还没收到结果");
            }
        }

        log.debug("收到结果");
    }
}
