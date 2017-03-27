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
    /**
     * Try to get a value from the given supplier, rethrowing any exception wrapped in a RuntimeException
     *
     * @param supplier the supplier to get a value from
     * @param <T> the type of element to get
     * @return the supplied value
     */
    public static <T> T rethrowR(@NonNull ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Try to run a runnable, rethrowing any exception wrapped in a RuntimeException
     *
     * @param runnable the runnable to run
     */
    public static void rethrow(@NonNull ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Transform a throwing function into a non throwing function by wrapping exceptions in RuntimeExceptions
     *
     * @param func the function to transform
     * @param <T> the functions input type
     * @param <R> the functions return type
     * @return the transformed non-throwing function
     */
    public static <T, R> Function<T, R> rethrowR(@NonNull ThrowingFunction<T, R> func) {
        return in -> {
            try {
                return func.apply(in);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    /**
     * Transform a throwing consumer into a non throwing consumer by wrapping exceptions in RuntimeExceptions
     *
     * @param consumer the consumer to transform
     * @param <T> the consumers input type
     * @return the transformed non-throwing consumer
     */
    public static <T> Consumer<T> rethrow(@NonNull ThrowingConsumer<T> consumer) {
        return in -> {
            try {
                consumer.accept(in);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
