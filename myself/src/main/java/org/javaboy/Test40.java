package org.javaboy;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.Test40")
public class Test40 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        phantomReference();
    }

    //强
    private static void strongReference() {
        MyObject myObject = new MyObject();
        System.out.println("gc before: " + myObject);

        myObject = null;
        System.gc();//人工开启GC，一般不用

        //暂停毫秒
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("gc after: " + myObject);
    }

    //软  开始gc的时候不会清除,gc完了内存还不够用,就会清除软引用
    private static void softReference() {
        SoftReference<MyObject> softReference = new SoftReference<>(new MyObject());
        System.out.println("-----softReference:" + softReference.get());
        System.gc();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----gc after内存够用: " + softReference.get());

        try {
            byte[] bytes = new byte[20 * 1024 * 1024];//20MB对象
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("-----gc after内存不够: " + softReference.get());
        }


    }

    //弱 只要gc就回收
    private static void weakReference() {
        WeakReference<MyObject> weakReference = new WeakReference<>(new MyObject());
        System.out.println("-----gc before 内存够用： " + weakReference.get());
        System.gc();
        System.out.println("-----gc after 内存够用： " + weakReference.get());
    }

    //虚

    /*
  1虚引用必须和引用队列(ReferenceQueue)联合使用
虚引用需要java.lang.ref.PhantomReference类来实现,顾名思义，
就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的
生命周期。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，
在任何时候都可能被垃圾回收器回收，它不能单独:使用也不能通过它访问对象，虚引用必须和引用队列(ReferenceQueue)联合使用。
2 PhantomReference的get方法总是返回null
虚引用的主要作用是跟踪对象被垃圾回收的状态。仅仅是提供了一种确保对象被finalize以后，
做某些事情的通知机制。PhantomReference的get方法总是返回null，因此无法访问对应的引用对象。
3处理监控通知使用
换句话说，设置虚引用关联对象的唯一目的，就是在这个对象被收集器回收的时候收到
一个系统通知或者后续添加进一步的处理，用来实现比finalize机制更灵活的回收操作
(某一个对象被回收时给一个通知)
    */
    private static void phantomReference() {
        MyObject myObject = new MyObject();
        ReferenceQueue<MyObject> referenceQueue = new ReferenceQueue<>();
        PhantomReference<MyObject> phantomReference = new PhantomReference<>(myObject, referenceQueue);
        System.out.println(phantomReference.get());//null

        List<byte[]> list = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                list.add(new byte[1 * 1024 * 1024]);//1mb
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(phantomReference.get() + "\t" + "list add ok");
            }
        }, "t1").start();
        new Thread(() -> {
            while (true) {
                Reference<? extends MyObject> poll = referenceQueue.poll();
                if (poll != null) {
                    System.out.println("有虚对象回收加入了引用队列");
                    break;
                }
            }
        }, "t2").start();
    }
}

class MyObject {
    @Override
    protected void finalize() {
        System.out.println("-------invoke finalize method~!!!");
    }
}
/**/

