package cn.itcast.n8;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestWordCount {
    public static void main(String[] args) {
        demo(
                // 创建 map 集合
                // 创建 ConcurrentHashMap 对不对？
                () -> new ConcurrentHashMap<String, LongAdder>(8, 0.75f, 8),

                (map, words) -> {
                    for (String word : words) {

                        // 如果缺少一个 key，则计算生成一个 value , 然后将  key value 放入 map ,不缺少则返回对应的value
                        //                  a      0
                        LongAdder value = map.computeIfAbsent(word, (key) -> {
                            return new LongAdder();//默认值0
                        });
                        // 执行累加
                        value.increment(); // 2

                        /*// 检查 key 有没有
                        Integer counter = map.get(word); //单个方法是原子的  比如两个线程依次访问完,拿到3
                        int newValue = counter == null ? 1 : counter + 1;      //3+1=4
                        // 没有 则 put
                        map.put(word, newValue); //组合起来就不是了   第一个线程,设置成4,然后第二个线程也设置成4*/
                    }
                }
        );
    }


    private static void demo2() {

        Map<String, Integer> collect = IntStream.range(1, 27).parallel()
                .mapToObj(idx -> readFromFile(idx))
                .flatMap(list -> list.stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(w -> 1)));
        System.out.println(collect);
    }

    private static <V> void demo(Supplier<Map<String, V>> supplier, BiConsumer<Map<String, V>, List<String>> consumer) {
        Map<String, V> counterMap = supplier.get();
        // key value
        // a   200
        // b   200
        List<Thread> ts = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            int idx = i;
            Thread thread = new Thread(() -> {//定义26个线程
                List<String> words = readFromFile(idx);
                consumer.accept(counterMap, words);
            });
            ts.add(thread);
        }

        ts.forEach(t -> t.start());//让子线程全部启动
        ts.forEach(t -> {
            try {
                t.join();//主线程等全部子线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(counterMap);
    }

    public static List<String> readFromFile(int i) {
        ArrayList<String> words = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("tmp/" + i + ".txt")))) {
            while (true) {
                String word = in.readLine();
                if (word == null) {
                    break;
                }
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
