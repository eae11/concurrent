package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/*
 ctl 高3位表示线程状态,低29位表示线程数量
 // 执行任务
void execute(Runnable command);
// 提交任务 task，用返回值 Future 获得任务执行结果
<T> Future<T> submit(Callable<T> task);
// 提交 tasks 中所有任务
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
 throws InterruptedException;
// 提交 tasks 中所有任务，带超时时间
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
 long timeout, TimeUnit unit)
 throws InterruptedException;
// 提交 tasks 中所有任务，哪个任务先成功执行完毕，返回此任务执行结果，其它任务取消
<T> T invokeAny(Collection<? extends Callable<T>> tasks)
 throws InterruptedException, ExecutionException;

 */
@Slf4j(topic = "c.Test04")
public class Test04 {
    public static void main(String[] args) {
      /*  //核心线程数,最大线程数(救急线程等于最大-核心线程数),救急线程存活时间,时间单位,阻塞队列,线程工厂可以为创建线程时起名字,拒绝策略
        //任务来了核心线程执行,核心线程满了放到阻塞队列里,阻塞队列满了救急线程上,执行完了会销毁,救急线程如果次数也满了,直接执行拒绝策略
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 3, 1, TimeUnit.SECONDS, new ArrayBlockingQueue(2));
        for (int i = 0; i < 5; i++) {
            int j = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                        log.debug("{}", j);
                }
            });
        }*/

        m3();

    }

    public static void m1() {
            /*
    public static ExecutorService newFixedThreadPool(int nThreads) {
 return new ThreadPoolExecutor(nThreads, nThreads,
 0L, TimeUnit.MILLISECONDS,
 new LinkedBlockingQueue<Runnable>());
}
特点
核心线程数 == 最大线程数（没有救急线程被创建），因此也无需超时时间
阻塞队列是无界的，可以放任意数量的任务
评价 适用于任务量已知，相对耗时的任务
    */
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "my_pool" + t.getAndAdd(1));
            }
        });
        //执行完了,两个核心线程wait不会被销毁
        for (int i = 0; i < 3; i++) {
            int j = i + 1;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    log.debug("{}", j);
                }
            });
        }
    }

    public static void m2() {
            /*
    public static ExecutorService newCachedThreadPool() {
 return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
 60L, TimeUnit.SECONDS,
 new SynchronousQueue<Runnable>());
}

特点
核心线程数是 0， 最大线程数是 Integer.MAX_VALUE，救急线程的空闲生存时间是 60s，意味着
全部都是救急线程（60s 后可以回收）救急线程可以无限创建
队列采用了 SynchronousQueue 实现特点是，它没有容量，没有线程来取是放不进去的（一手交钱、一手交
货）
评价整个线程池表现为线程数会根据任务量不断增长，没有上限，当任务执行完毕，空闲 1分钟后释放线
程。适合任务数比较密集，但每个任务执行时间较短的情况

    */
        ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
            AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "my_pool" + t.getAndAdd(1));
            }
        });
        for (int i = 0; i < 3; i++) {
            int j = i + 1;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    log.debug("{}", j);
                }
            });
        }
    }

    public static void m3() {
            /*
  public static ExecutorService newSingleThreadExecutor() {
 return new FinalizableDelegatedExecutorService
 (new ThreadPoolExecutor(1, 1,
 0L, TimeUnit.MILLISECONDS,
 new LinkedBlockingQueue<Runnable>()));
}
使用场景：
希望多个任务排队执行。线程数固定为 1，任务数多于 1 时，会放入无界队列排队。任务执行完毕，这唯一的线程
也不会被释放。
区别：
自己创建一个单线程串行执行任务，如果任务执行失败而终止那么没有任何补救措施，而线程池还会新建一
个线程，保证池的正常工作
Executors.newSingleThreadExecutor() 线程个数始终为1，不能修改
FinalizableDelegatedExecutorService 应用的是装饰器模式，只对外暴露了 ExecutorService 接口，因
此不能调用 ThreadPoolExecutor 中特有的方法
Executors.newFixedThreadPool(1) 初始时为1，以后还可以修改
对外暴露的是 ThreadPoolExecutor 对象，可以强转后调用 setCorePoolSize 等方法进行修改
    */
        ExecutorService pool = Executors.newSingleThreadExecutor(new ThreadFactory() {
            AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "my_pool" + t.getAndAdd(1));
            }
        });
        //此线程出现异常终止,还会在创建一个线程
        pool.execute(() -> {
            log.debug("1");
            int i = 1 / 0;
        });

        pool.execute(() -> {
            log.debug("2");
        });

        pool.execute(() -> {
            log.debug("3");
        });

    }
}
