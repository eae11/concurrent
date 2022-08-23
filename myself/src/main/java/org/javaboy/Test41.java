package org.javaboy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Slf4j(topic = "c.Test37")
public class Test41 {
    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(4);
        /*Integer i = pool.invoke(new AddTask1(5));//invoke返回结果 阻塞
        log.debug("{}", i);

        log.debug("主线程干其他事");*/

/*ForkJoinTask<Integer> f = pool.submit(new AddTask1(5));//submit返回ForkJoinTask本身
        Thread.sleep(1000);
        log.debug("{}", f.join());*/


        log.debug("{}", pool.invoke(new AddTask2(1, 10)));
        log.debug("{}", pool.invoke(new AddTask3(1, 10)));
    }
}

// 1~n 之间整数的和
@Slf4j(topic = "c.AddTask1")
/*
ForkJoinTask
ForkJoinTask : 基本任务，使用fork、join框架必须创建的对象，提供fork,join操作，常用的三个子类如下：

RecursiveAction无结果返回的任务
RecursiveTask有返回结果的任务
CountedCompleter无返回值任务，完成任务后可以触发回调

ForkJoinTask提供了两个重要的方法：
fork让task异步执行

join让task同步执行，可以获取返回值
*/

        //任务5依赖于任务4并行度不高
class AddTask1 extends RecursiveTask<Integer> {
    private int n;

    public AddTask1(int n) {
        this.n = n;
    }

    @SneakyThrows
    @Override
    protected Integer compute() {
        if (n == 1) {
            Thread.sleep(3000);
            log.debug("join {}", n);
            return n;
        }
        AddTask1 t1 = new AddTask1(n - 1);
        t1.fork();//让另一个线程去执行任务
        log.debug("fork {} + {}", n, t1);
        int result = n + t1.join();//此线程等待(阻塞)
        log.debug("join {} + {} = {}", n, t1, result);
        return result;

    }
}

//并行度高一点
@Slf4j(topic = "c.AddTask2")
class AddTask2 extends RecursiveTask<Integer> {
    private int begin;
    private int end;

    public AddTask2(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @SneakyThrows
    @Override
    protected Integer compute() {
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }
        if (end - begin == 1) {
            log.debug("join() {} + {} = {}", begin, end, end + begin);
            return end + begin;
        }
        int mid = (end + begin) / 2;
        AddTask2 t1 = new AddTask2(begin, mid - 1);
        t1.fork();
        AddTask2 t2 = new AddTask2(mid + 1, end);
        t2.fork();
        log.debug("fork() {} + {} + {} = ?", mid, t1, t2);

        int result = mid + t1.join() + t2.join();
        log.debug("join() {} + {} + {} = {}", mid, t1, t2, result);
        return result;

    }
}

//也可以这样拆
@Slf4j(topic = "c.AddTask3")
class AddTask3 extends RecursiveTask<Integer> {

    int begin;
    int end;

    public AddTask3(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" + begin + "," + end + '}';
    }

    @Override
    protected Integer compute() {
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }
        if (end - begin == 1) {
            log.debug("join() {} + {} = {}", begin, end, end + begin);
            return end + begin;
        }
        int mid = (end + begin) / 2;

        AddTask3 t1 = new AddTask3(begin, mid);
        t1.fork();
        AddTask3 t2 = new AddTask3(mid + 1, end);
        t2.fork();
        log.debug("fork() {} + {} = ?", t1, t2);

        int result = t1.join() + t2.join();
        log.debug("join() {} + {} = {}", t1, t2, result);
        return result;
    }
}

