package io.buybrain.util;

import io.buybrain.util.function.ThrowingConsumer;
import io.buybrain.util.function.ThrowingFunction;
import io.buybrain.util.function.ThrowingRunnable;
import io.buybrain.util.function.ThrowingSupplier;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import static io.buybrain.util.Exceptions.rethrow;
import static io.buybrain.util.Exceptions.rethrowR;

/**
 * Type that can be used to encode the result of a function, which can succeed or fail. Similar to Optional, but with
 * an error object added for failure cases. Can conveniently deal with throwing functions without try-catch constructs.
 */
@EqualsAndHashCode
public class Result<T, E extends Throwable> {
    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    /**
     * If the current value is OK, transform it into a new Result by applying a function.
     * 
     * @param op the function to apply, should return a Result object
     * @return the transformed result
     * @throws RuntimeException if the mapping function throws
     */
    public <R> Result<R, ?> map(@NonNull ThrowingFunction<T, Result<R, ?>> op) {
        if (isOk()) {
            return rethrowR(op.bind(value));
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is OK, transform it into a new Result by applying a function.
     * The function should return the raw value type T. It will be wrapped in OK if the function returns, and any
     * throwable thrown will be wrapped in an Error result.
     *
     * @param op the function to apply, should return the raw value that will be automatically wrapped
     * @return the transformed result
     */
    public <R> Result<R, ?> tryMap(@NonNull ThrowingFunction<T, R> op) {
        if (isOk()) {
            return trying(op.bind(value));
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is OK, yield a new Result by calling a supplier.
     *
     * @param op the supplier to call, should return a Result object
     * @return the new result
     * @throws RuntimeException if the supplier throws
     */
    public <R> Result<R, ?> andThen(@NonNull ThrowingSupplier<Result<R, ?>> op) {
        if (isOk()) {
            return rethrowR(op);
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is OK, yield a new Result by calling a supplier.
     * The supplier return the raw value type T. It will be wrapped in OK if the supplier returns, and any
     * throwable thrown will be wrapped in an Error result.
     *
     * @param op the supplier to call, should return a Result object
     * @return the new result
     */
    public <R> Result<R, ?> andThenTry(@NonNull ThrowingSupplier<R> op) {
        if (isOk()) {
            return trying(op);
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is OK, pass it to a consumer and return an empty OK result.
     *
     * @param op the consumer to call
     * @return the new (empty) result
     * @throws RuntimeException if the consumer throws
     */
    public <R> Result<R, ?> andThen(@NonNull ThrowingConsumer<T> op) {
        if (isOk()) {
            rethrow(op.bind(value));
            return ok();
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is OK, pass it to a consumer and return an empty OK result.
     * If the consumer throws, the error will be wrapped in an Error result.
     *
     * @param op the consumer to call
     * @return the new (empty) result
     */
    public Result<?, ?> andThenTry(@NonNull ThrowingConsumer<T> op) {
        if (isOk()) {
            return trying(op.bind(value));
        } else {
            return this;
        }
    }

    /**
     * If the current value is OK, call a runnable and return an empty OK result.
     *
     * @param op the runnable to run
     * @return the new (empty) result
     * @throws RuntimeException if the runnable throws
     */
    public <R> Result<R, ?> andThen(@NonNull ThrowingRunnable op) {
        if (isOk()) {
            rethrow(op);
            return ok();
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is OK, call a runnable and return an empty OK result.
     * f the runnable throws, the error will be wrapped in an Error result.
     *
     * @param op the runnable to run
     * @return the new (empty) result
     * @throws RuntimeException if the runnable throws
     */
    public <R> Result<R, ?> andThenTry(@NonNull ThrowingRunnable op) {
        if (isOk()) {
            return trying(op);
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    /**
     * If the current value is an error, transform it into a default value.
     * 
     * @param defaultValue the default value
     * @return the new OK result wrapping the default value
     */
    public Result<T, ?> orElse(T defaultValue) {
        if (isOk()) {
            return this;
        } else {
            return ok(defaultValue);
        }
    }

    public Result<T, ?> mapErr(@NonNull ThrowingFunction<E, Result<T, ?>> op) {
        if (isOk()) {
            return this;
        } else {
            return rethrowR(op.bind(error));
        }
    }

    public Result<T, ?> tryMapErr(@NonNull ThrowingFunction<E, T> op) {
        if (isOk()) {
            return this;
        } else {
            return trying(op.bind(error));
        }
    }

    public Result<T, ?> orElse(@NonNull ThrowingSupplier<Result<T, ?>> op) {
        if (isOk()) {
            return this;
        } else {
            return rethrowR(op);
        }
    }

    public Result<T, ?> orElseTry(@NonNull ThrowingSupplier<T> op) {
        if (isOk()) {
            return this;
        } else {
            return trying(op);
        }
    }

    public Result<T, ?> orElse(@NonNull ThrowingConsumer<E> op) {
        if (isOk()) {
            return this;
        } else {
            rethrow(op.bind(error));
            return ok();
        }
    }

    public Result<T, ?> orElseTry(@NonNull ThrowingConsumer<E> op) {
        if (isOk()) {
            return this;
        } else {
            return trying(op.bind(error));
        }
    }

    public Result<T, ?> orElse(@NonNull ThrowingRunnable op) {
        if (isOk()) {
            return this;
        } else {
            rethrow(op);
            return ok();
        }
    }

    public Result<T, ?> orElseTry(@NonNull ThrowingRunnable op) {
        if (isOk()) {
            return this;
        } else {
            return trying(op);
        }
    }

    public T get() throws E {
        if (isOk()) {
            return value;
        } else {
            throw error;
        }
    }
    
    public T getUnsafe() {
        return value;
    }

    public E getError() {
        return error;
    }

    public static <T> Result<T, ?> ok() {
        return new Result<>(null, null);
    }

    public static <T> Result<T, ?> trying(@NonNull ThrowingSupplier<T> op) {
        try {
            return ok(op.get());
        } catch (Throwable ex) {
            return err(ex);
        }
    }

    public static <T> Result<T, ?> trying(@NonNull ThrowingRunnable op) {
        try {
            op.run();
            return ok();
        } catch (Throwable ex) {
            return err(ex);
        }
    }

    public boolean isOk() {
        return error == null;
    }

    public static <T> Result<T, ?> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T, E extends Throwable> Result<T, E> err(@NonNull E error) {
        return new Result<>(null, error);
    }
}
