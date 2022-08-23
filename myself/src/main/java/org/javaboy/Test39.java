package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.Test39")
public class Test39 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyData myData = new MyData();
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            for (int i = 0; i < 10; i++) {
                pool.submit(() -> {
                    try {
                        Integer before = myData.t.get();
                        myData.add();
                        Integer after = myData.t.get();
                        log.debug("before{},after{}", before, after);
                    } finally {
                        //一定要remove
                        //这里是10个线程,每个线程都要一个threadlocal,但是使用线程池,里面3个核心线程去执行这10个任务
                        //会也就是说真正使用的就3个线程,所以每次执行完都删除掉
                        //不然其他任务可能会用到核心线程里的threadlocal(别人用过的)  也还会导致内存泄漏
                        myData.t.remove();
                    }

                });
            }
        } finally {
            pool.shutdown();
        }
    }
}

class MyData {
    ThreadLocal<Integer> t = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return 0;
        }
    };
    //ThreadLocal<Integer> t = ThreadLocal.withInitial(() -> 0);

    public void add() {
        t.set(1 + t.get());
    }
}