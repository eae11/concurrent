package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@Slf4j(topic = "c.Test16")
public class Test16 {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<Integer> ids = Mailboxes.getIds();
        for (Integer id : ids) {
            new Postman(id, "内容" + id).start();
        }
    }
}

@Slf4j(topic = "c.People")
class People extends Thread {
    @Override
    public void run() {
        GuardedObject2 guardedObject2 = Mailboxes.createGuardedObject2();
        log.debug("开始收信 id:{}", guardedObject2.getId());
        Object mail = guardedObject2.get(2000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject2.getId(), mail);

    }
}

@Slf4j(topic = "c.Postman")
class Postman extends Thread {
    private int id;
    //内容
    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObject2 guardedObject2 = Mailboxes.getGuardedObject2(id);
        log.debug("送信 id:{}, 内容:{}", id, mail);
        //投递内容
        guardedObject2.complete(mail);
    }
}

class Mailboxes {
    private static Map<Integer, GuardedObject2> boxes = new Hashtable<>();
    private static int id = 1;

    // 产生唯一 id
    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObject2 createGuardedObject2() {
        GuardedObject2 go = new GuardedObject2(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static GuardedObject2 getGuardedObject2(int id) {
        GuardedObject2 go = boxes.remove(id);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

class GuardedObject2 {
    //结果
    private Object response;
    // 标识
    private int id;

    public GuardedObject2(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

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