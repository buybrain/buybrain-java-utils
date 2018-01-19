package io.buybrain.util;

import java.util.List;

public class Nulls {
    /**
     * Find the first non-null element
     *
     * @param elements the elements to coalesce
     * @param <T> the type of the elements
     * @return the first non-null element, or null if no such element exists
     */
    public static <T> T coalesce(T... elements) {
        for(T elem : elements) {
            if (elem != null) {
                return elem;
            }
        }
        return null;
    }

    /**
     * Find the first non-null element
     *
     * @param elements the elements to coalesce
     * @param <T> the type of the elements
     * @return the first non-null element, or null if no such element exists
     */
    public static <T> T coalesce(List<T> elements) {
        for(T elem : elements) {
            if (elem != null) {
                return elem;
            }
        }
        return null;
    }
}
