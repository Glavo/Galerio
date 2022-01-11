package org.glavo.galerio.util;

import java.util.*;

public class CollectionUtils {
    public static <T> Set<T> setOf() {
        return Collections.emptySet();
    }

    public static <T> Set<T> setOf(T value0) {
        HashSet<T> set = new HashSet<>();
        set.add(value0);
        return set;
    }

    public static <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
