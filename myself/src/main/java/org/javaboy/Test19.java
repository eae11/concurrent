package org.javaboy;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test27")
public class Test19 {
    public static void main(String[] args) {
        WaitNotify waitNotify = new WaitNotify(1, 6);
        new Thread(() -> {
            waitNotify.print("a", 1, 2);
        }).start();
        new Thread(() -> {
            waitNotify.print("b", 2, 3);
        }).start();
        new Thread(() -> {
            waitNotify.print("c", 3, 1);
        }).start();
    }
}

/*
输出内容  标记 下一个标记
a           1       2
b           2       3
c           3       1
 */
@Slf4j(topic = "c.WaitNotify")
class WaitNotify {
    private int flag;
    private int loopNumber;

    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    public void print(String str, int flag, int next) {
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this) {
                while (flag != this.flag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug(str);
                this.flag = next;
                this.notifyAll();
            }

        }
    }
}
