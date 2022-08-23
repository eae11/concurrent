package org.javaboy;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j(topic = "c.TestPool_01")
public class TestPool_01 {
    public static void main(String[] args) {
       /* ThreadPool threadPool = new ThreadPool(2, 1000, 10, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 5; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }*/

        ThreadPool threadPool = new ThreadPool(2, 1000, 10, TimeUnit.MILLISECONDS, new RejectPolicy<Runnable>() {
            @Override
            public void reject(BlockingQueue queue, Runnable task) {

                // 1. 死等
//            queue.put(task);
                // 2) 带超时等待
            queue.offer(task, 1500, TimeUnit.MILLISECONDS);
                // 3) 让调用者放弃任务执行
            //log.debug("放弃{}", task);
                // 4) 让调用者抛出异常
            //throw new RuntimeException("任务执行失败 " + task);
                // 5) 让调用者自己执行任务 也就是在主线程里去执行
                //log.debug("执行拒绝策略");
                //task.run();
            }
        });
        for (int i = 0; i < 15; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    //Thread.sleep(1000000000L);//让任务执行久一点,然后阻塞队列也满了,剩下的3个任务执行拒绝策略
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }


        log.debug("主线程结束");
    }
}

@Slf4j(topic = "c.ThreadPool")
class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private HashSet<Worker> workers = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 获取任务时的超时时间
    private long timeout;

    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, int queueCapcity, TimeUnit timeUnit, RejectPolicy<Runnable> rejectPolicy) {
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
    }

    public ThreadPool(int coreSize, long timeout, int queueCapcity, TimeUnit timeUnit) {
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    // 执行任务
    public void execute(Runnable task) {
        // 当任务数没有超过 coreSize 时，直接交给 worker 对象执行
        // 如果任务数超过 coreSize 时，加入任务队列暂存
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker{}, {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                /*
                // 1) 死等
                // 2) 带超时等待
                // 3) 让调用者放弃任务执行
                // 4) 让调用者抛出异常
                // 5) 让调用者自己执行任务
                很多选择下面这两张方式相当于写死了,或者代码要写很多if else,我们用策略模式,抽象一个拒绝策略接口,将具体实现交给调用者,
                */
                //死等,主线程阻塞了
                //taskQueue.put(task);
                //带超时时间
                //taskQueue.offer(task, 1, TimeUnit.SECONDS);

                //抽象一个拒绝策略接口,将具体实现交给调用者,
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1) 当 task 不为空，执行任务
            // 2) 当 task 执行完毕，再接着从任务队列获取任务并执行
            //while (task != null || (task = taskQueue.take()) != null) {
            //带超时时间
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    log.debug("正在执行...{}", task);
                    task.run();
                } finally {
                    task = null;
                }
            }
            //当任务数量大于核心线程(work)的时候核心线程不会被移除,会一直复用执行任务,当任务执行完了
            // (带有超时时间的这段时间内没任务的话)核心线程会被移除
            synchronized (workers) {
                log.debug("worker 被移除{}", this);
                workers.remove(this);
            }

        }
    }
}

@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T> {
    // 1. 任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2. 锁
    private ReentrantLock lock = new ReentrantLock();

    // 3. 生产者条件变量
    private Condition fullWaitSet = lock.newCondition();
    // 4. 消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5. 容量
    private int capcity;

    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    // 阻塞获取
    public T take() {
        lock.lock();
        try {
            //当阻塞队列里没有task时,去emptyWaitSet等待
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.pollFirst();
            log.debug("从队列里获取任务{}", t);
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    // 阻塞添加
    public void put(T task) {
        lock.lock();
        try {
            //当阻塞队列里task满了,去fullWaitSet等待
            while (queue.size() == capcity) {
                try {
                    log.debug("等待加入任务队列 {} ...", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            //当线程池任务没有满,就继续添加任务
            queue.offerLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }

    }

    // 带超时阻塞获取
    public T poll(long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            // 将 timeout 统一转换为 纳秒
            long nanos = timeUnit.toNanos(timeout);
            //当阻塞队列里没有task时,去emptyWaitSet等待
            while (queue.isEmpty()) {
                try {
                    if (nanos < 0) {
                        return null;
                    }
                    //如果没等够时间就被唤醒,下次进入还得等这么长时间 所有前面加个判断防止虚假唤醒
                    //返回值是剩余时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.pollFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 带超时时间阻塞添加
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            Long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capcity) {
                try {
                    if (nanos < 0) {
                        return false;
                    }
                    log.debug("等待加入任务队列 {} ...", task);
                    //剩余时间
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            boolean b = queue.offerLast(task);
            emptyWaitSet.signal();
            return b;
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否满
            if (queue.size() == capcity) {
                //执行拒绝策略,调用者自行决定
                rejectPolicy.reject(this, task);
            } else {
                log.debug("加入任务队列 {}", task);
                queue.offerLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}