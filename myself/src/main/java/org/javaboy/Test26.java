package org.javaboy;

public class Test26 {


}

final class Singleton {
    private static volatile Singleton INSTANCE = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();//有可能先执行赋值(指令重排序),在调用构造方法,然后其他线程判断instance为null,又重新创建了一个对象覆盖掉以前的
                }
            }
        }
        return INSTANCE;
    }
}
