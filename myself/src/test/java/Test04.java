import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Test04 {
    public static void main(String[] args) {
        Consumer<String> c = (s) -> System.out.println(s);
        c.accept("aa");
        HashMap map = new HashMap();
        new ConcurrentHashMap<>();
    }
}


