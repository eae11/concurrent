package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


@Slf4j(topic = "c.Test48")
public class Test48 {
    public static void main(String[] args) throws InterruptedException {
        demo(
                () -> {
                    return new ConcurrentHashMap<String, LongAdder>(8, 0.75f, 8);
                },
                (map, words) -> {
                    for (String word : words) {
                        // 如果缺少一个 key，则计算生成一个 value , 然后将  key value 放入 map ,不缺少则返回对应的value
                        //                  a      0
                        LongAdder longAdder = map.computeIfAbsent(word, (key) -> {
                            return new LongAdder();//默认值0
                        });
                        longAdder.increment();
                    }
                }
        );

    }

    public static <V> void demo(Supplier<Map<String, V>> supplier, BiConsumer<Map<String, V>, List<String>> consumer) {
        Map<String, V> map = supplier.get();
        List<Thread> ts = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            int idx = i;
            Thread thread = new Thread(() -> {//定义26个线程
                List<String> words = readFromFile(idx);
                consumer.accept(map, words);
            });
            ts.add(thread);
        }

        ts.forEach((t) -> {
            t.start();
        });
        ts.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(map);
    }

    public static List<String> readFromFile(int i) {
        List<String> list = new ArrayList<>();
        String s;
        try (BufferedReader in = new BufferedReader(new FileReader("tmp/" + i + ".txt"))) {
            while ((s = in.readLine()) != null) {
                list.add(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

