package local;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Util {

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object object) {
        return (T) object;
    }

    public static <T> List<T> newLinkedList(T... elements) {
        List<T> list = new LinkedList<>();
        Collections.addAll(list, elements);
        return list;
    }

    private Util() {
    }
}
