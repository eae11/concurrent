package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "c.Test15")
public class Test15 {
    static Thread t2 = null;

    public static void main(String[] args) {
       /* Thread t1 = new Thread(() -> {
            //等待结果
            try {
                log.debug("等待结果");
                t2.join();
                log.debug("完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }, "t1");
        t2 = new Thread(() -> {

            log.debug("执行下载");

        }, "t2");

        t1.start();
        t2.start();*/

        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            //等待结果
            log.debug("等待结果");
            List<String> list = (List<String>) guardedObject.get(200);
            log.debug("结果大小{}", list);

        }, "t1").start();
        new Thread(() -> {
            //执行下载
            log.debug("开始下载");
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL("https://www.baidu.com/").openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //下载好了把结果传过去
            log.debug("下载完成");
            //对比join,要等到此线程结束,另一个线程才能开始执行,而这种不用等到此线程结束
            //完成这个guardedObject.complete(lines);方法后另一个线程就能执行
            //t2得设置成全局的不方便
            guardedObject.complete(lines);
        }, "t2").start();
    }
}

class GuardedObject {
    //结果
    private Object response;

    public Object get() {
        synchronized (this) {
            while (response == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    //带超时时间的获取结果
    public Object get(long timeout) {
        if (timeout == 0) {
            get();
        }
        synchronized (this) {
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            //没有结果
            while (response == null) {
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间时，退出循环
                if (waitTime <= 0) {
                    break;
                } else {
                    try {
                        this.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 求得经历时间
                passedTime = System.currentTimeMillis() - begin;
            }
        }
        return response;
    }

    // 产生结果
    public void complete(Object response) {
        synchronized (this) {
            this.response = response;
            this.notifyAll();
        }
    }
}
