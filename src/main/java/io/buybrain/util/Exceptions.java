package io.buybrain.util;

import io.buybrain.util.function.ThrowingConsumer;
import io.buybrain.util.function.ThrowingFunction;
import io.buybrain.util.function.ThrowingRunnable;
import io.buybrain.util.function.ThrowingSupplier;
import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility functions for dealing with checked exceptions in streams by wrapping them in runtime exceptions.
 *
 * The functions in this class are called `rethrow` and `rethrowR`. The R stands for Returning. The reason for this
 * postfix is to deal with cases where the compiler cannot infer if a lambda function is meant to return or not, which
 * would make a Function and Consumer ambiguous, as well as a Supplier and Runnable.
 */
public class Exceptions {
    public static <T> T rethrowR(@NonNull ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void rethrow(@NonNull ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T, R> Function<T, R> rethrowR(@NonNull ThrowingFunction<T, R> func) {
        return in -> {
            try {
                return func.apply(in);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    public static <T> Consumer<T> rethrow(@NonNull ThrowingConsumer<T> consumer) {
        return in -> {
            try {
                consumer.accept(in);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
