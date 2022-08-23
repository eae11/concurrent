package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.Test36")
public class Test36 {
    public static void main(String[] args) throws InterruptedException {
        m9();
    }

    //合并演示
    private static void m9() throws InterruptedException {
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> {
            return 50;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 100;
        }), (x, y) -> {
            return x + y;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 200;
        }), (x, y) -> {
            return x + y;
        });
        log.debug("{}", result.join());
    }

    /*两个completionStage任务都完成后，最终能把两个任务的结果一起交给thenCombine来处理
    先完成的先等着，等待其它分支任务*/
    private static void m8() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        CompletableFuture<String> f1, f2;
        try {
            f1 = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(20);
                    log.debug("1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "1";
            }, pool);
            f2 = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                    log.debug("2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "2";
            }, pool);
        } finally {
            pool.shutdown();
        }
        //
        CompletableFuture<String> result = f1.thenCombine(f2, (v1, v2) -> {
            log.debug("{},{}", v1, v2);//两个结果都拿到
            return v1 + v2;
        });
        log.debug("{}", result.join());
    }

    //哪个任务快谁就用谁
    private static void m7() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        CompletableFuture<String> f1, f2;
        try {
            f1 = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(20);
                    log.debug("1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "1";
            }, pool);
            f2 = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                    log.debug("2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "2";
            }, pool);
        } finally {
            pool.shutdown();
        }
        //
        CompletableFuture<String> result = f1.applyToEither(f2, (f) -> {
            log.debug("{}", f);//那个任务快返回那个
            return f;
        });
        log.debug("{}", result.join());
    }

    private static void m6() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            //thenRun
            //任务A执行完执行B,B不需要A的结果
            CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(1000);
                            log.debug("1");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "1";
                    }, pool)
                    .thenRun(() -> {
                        log.debug("2");
                    });

        } finally {
            pool.shutdown();
        }
        log.debug("主线程干其他");
    }

    private static void m5() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            //thenAccept
            //任务A执行完执行任务B,B需要A的结果,但是任务B无返回值
            CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(1000);
                            log.debug("1");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "1";
                    }, pool)
                    .thenApply((s) -> {
                        log.debug("2");
                        return s + "2";
                    }).thenApply((s) -> {
                        return s + "3";
                        //无返回值对任务结果进行消费
                    }).thenAccept((s) -> {
                        log.debug("{}", s);
                    });

        } finally {
            pool.shutdown();
        }
        log.debug("主线程干其他");
    }

    private static void m4() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            //handle
            //有异常也可以继续处理
            CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(1000);
                            log.debug("1");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int i = 10 / 0;
                        //出异常了没处理返回null
                        return "1";
                    }, pool)
                    //第一个参数,第二个异常参数
                    .handle((s, e) -> {
                        log.debug("2");
                        e.printStackTrace();
                        return s + "2";//"null2"
                    }).handle((s, e) -> {
                        e.printStackTrace();
                        return s + "3";//"null23"
                    })
                    .whenComplete((v, e) -> {
                        if (e == null) {
                            log.debug("{}", v);
                        }
                    }).exceptionally((e) -> {
                        e.printStackTrace();
                        return null;
                    });
        } finally {
            pool.shutdown();
        }
        log.debug("主线程干其他");
    }

    private static void m3() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            //计算结果存在依赖关系(当前步骤错,不走下一步)当前步骤有异常就停
            //thenApply
            //任务A执行完执行任务B,B需要A的结果,任务B也有返回值
            CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(1000);
                            log.debug("1");
                            int i = 10 / 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        return "1";
                    }, pool).thenApply((s) -> {
                        log.debug("2");
                        return s + "2";
                    }).thenApply(s -> s + "3")
                    .whenComplete((v, e) -> {
                        if (e == null) {
                            log.debug("{}", v);
                        }
                    }).exceptionally((e) -> {
                        e.printStackTrace();
                        return null;
                    });
        } finally {
            pool.shutdown();
        }
        log.debug("主线程干其他");
    }

    private static void m2() throws InterruptedException {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "666";
        });
        Thread.sleep(1500);
        boolean flag = f.complete("xxx");
        String now = f.join();
        //任务完成就返回任务值,flag为false,否则就返回"xxx",flag为true
        log.debug("{},{}", flag, now);
    }

    private static void m1() {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "666";
        });
        //f完成了就返回结果,否则返回valueIfAbsent(自己给的值)
        //Thread.sleep(1500);
        String now = f.getNow("xxx");
        log.debug("{}", now);
    }
}
