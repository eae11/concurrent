package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/*
与前面的保护性暂停中的 GuardObject 不同，不需要产生结果和消费结果的线程一一对应
消费队列可以用来平衡生产和消费的线程资源
生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
消息队列是有容量限制的，满时不会再加入数据，空时不会再消耗数据
JDK 中各种阻塞队列，采用的就是这种模式
*/
@Slf4j(topic="c.Test17")
public class Test17 {

    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(2);
        //三个线程往消息队列里放消息
        for (int i = 0; i < 3; i++) {
            int j = i;
            new Thread(() -> {
                messageQueue.put(new Message(j, "值" + j));
            }, "生产者" + i).start();
        }
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message take = messageQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"消费者").start();

    }

}

//  java 线程之间通信
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    private LinkedList<Message> list = new LinkedList<>();
    private int capcity;

    public MessageQueue(int capcity) {
        this.capcity = capcity;
    }

    public void put(Message message) {
        synchronized (list) {
            while (list.size() == capcity) {
                try {
                    log.debug("队列已满, 生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.offerLast(message);
            log.debug("已生产消息 {}", message);
            list.notifyAll();
        }
    }

    public Message take() {
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    log.debug("队列为空, 消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = list.pollFirst();
            log.debug("已消费消息 {}", message);
            list.notifyAll();
            return message;
        }
    }
}

final class Message {
    private int id;
    private Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}