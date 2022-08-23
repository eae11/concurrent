package test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class TestHashMap {
    public static void main(String[] args) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "2");
        Set<Object> keySet = map.keySet();
        /*for (Object o : keySet) {
            map.remove("2");//语法糖实际上是迭代器
        }*/
        Iterator<Object> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            /* if (modCount != expectedModCount)
                throw new ConcurrentModificationException()modCount为3,expectedModCount在调用调用迭代器的构造方法时赋的值为2;*/
            String next = (String) iterator.next();
            if (next.equals("2")) {
                //map.remove(next);//删除以后modCount变为3
                iterator.remove();// iterator.remove()方法会在修改完modCount后重新赋值给expectedModCount
            }
            /*
                //fast-fail容错机制在并发下一个线程在遍历,一个在删除不让你代码继续执行下去了
                解决此异常的办法
                调用迭代器的remove方法

                 public void remove() {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Object k = current.key;
            current = null;
            HashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;每次调用完重新赋下值*/

        }
    }
}