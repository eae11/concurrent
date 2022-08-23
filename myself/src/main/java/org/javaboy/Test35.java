package org.javaboy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 案例说明：电商比价需求，模拟如下情况：
 * <p>
 * 1需求：
 * 1.1 同一款产品，同时搜索出同款产品在各大电商平台的售价;
 * 1.2 同一款产品，同时搜索出本产品在同一个电商平台下，各个入驻卖家售价是多少
 * <p>
 * 2输出：出来结果希望是同款产品的在不同地方的价格清单列表，返回一个List<String>
 * 《mysql》 in jd price is 88.05
 * 《mysql》 in dangdang price is 86.11
 * 《mysql》 in taobao price is 90.43
 * <p>
 * 3 技术要求
 * 3.1 函数式编程
 * 3.2 链式编程
 * 3.3 Stream流式计算
 */
@Slf4j(topic = "c.Test35")
public class Test35 {
    static List<NetMall> list = Arrays.asList(new NetMall("jd"), new NetMall("dangdang"), new NetMall("taobao"), new NetMall("pdd"), new NetMall("tmall"));

    public static void main(String[] args) {
        /*List<String> list = getPriceByCompletableFuture2(Test35.list, "mysql");
        log.debug("{}", list);*/
        getPriceByCompletableFuture3(Test35.list, "mysql");
    }

    public static void getPriceByCompletableFuture3(List<NetMall> list, String productName) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        long start = System.currentTimeMillis();
        List<String> prices;
        try {
            prices = new ArrayList<>();
            for (NetMall netMall : list) {

                CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                    return String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName));
                    //如果说要把多个线程的任务执行完后汇总,(没办法通知主线程)这种方式还是不行,
                }, pool).whenComplete((v, e) -> {
                    if (e == null) {
                        prices.add(v);
                        log.debug("{}", prices);
                    }
                });

            }
        } finally {
            pool.shutdown();
        }
        //log.debug("{}", prices);

        //log.debug("花费时间{}", System.currentTimeMillis() - start);
    }

    //不用流式编程
    public static List<String> getPriceByCompletableFuture2(List<NetMall> list, String productName) {
        long start = System.currentTimeMillis();

      /*  List<CompletableFuture<String>> collect = list.stream().map((netMall) -> {
                    return CompletableFuture.supplyAsync(() -> {
                        return String.format(productName + " in %s price is %.2f",
                                netMall.getNetMallName(),
                                netMall.calcPrice(productName));

                    });

                })
                .collect(Collectors.toList());*/
        List<CompletableFuture<String>> collect = new ArrayList<>();
        for (NetMall netMall : list) {

            CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                return String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName));
            });
            collect.add(f);
        }
        List<String> prices = new ArrayList<>();
        for (CompletableFuture<String> f : collect) {
            prices.add(f.join());
        }

        log.debug("花费时间{}", System.currentTimeMillis() - start);
        return prices;
    }

    public static List<String> getPriceByCompletableFuture(List<NetMall> list, String productName) {
        long start = System.currentTimeMillis();
        /*
        list.stream()先弄成流一样,.map()就是要对里面元素进行操作,里面要给一个Function(有参数,有返回值)
        参数就是list泛型里面的每个元素,注意map()返回值是Stream<>泛型,因为要进行流式编程,泛型里面的值就是function返回值的类型
        .collect()操作可以把流转成处理过后的集合
        */
        List<String> prices = list
                .stream()
                .map((netMall) -> {
                    return CompletableFuture.supplyAsync(() -> {
                        return String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName));

                    });

                })
                .collect(Collectors.toList())
                .stream()
                .map((s) -> {
                    return s.join();
                })
                .collect(Collectors.toList());
        log.debug("花费时间{}", System.currentTimeMillis() - start);
        return prices;
    }

    public static List<String> getPrice(List<NetMall> list, String productName) {
        long start = System.currentTimeMillis();
        List<String> prices = list.stream()
                //function有参数有返回值,Consumer有参数无返回值,Supplier无参数有返回值,Predicate有参数返回boolean值
                .map((netMall) -> {                             //小数点后两位
                    return String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName));
                }).collect(Collectors.toList());
        log.debug("花费时间{}", System.currentTimeMillis() - start);
        return prices;
    }
}

class NetMall {
    @Getter
    private String netMallName;

    public NetMall(String netMallName) {
        this.netMallName = netMallName;
    }

    public double calcPrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //[0,1)
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}