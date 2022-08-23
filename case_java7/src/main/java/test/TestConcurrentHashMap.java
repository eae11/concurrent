package test;

import java.util.concurrent.ConcurrentHashMap;

public class TestConcurrentHashMap {
    public static void main(String[] args) {
        new ConcurrentHashMap(64, 0.75f, 31);
        //new ConcurrentHashMap(8, 0.75f, 8);
    }
}
