package io.buybrain.util;

import io.buybrain.util.function.ThrowingConsumer;
import io.buybrain.util.function.ThrowingFunction;
import io.buybrain.util.function.ThrowingRunnable;
import io.buybrain.util.function.ThrowingSupplier;
import lombok.EqualsAndHashCode;

import static io.buybrain.util.Exceptions.rethrow;
import static io.buybrain.util.Exceptions.rethrowR;

/**
 * Type that can be used to encode the result of a function, which can succeed or fail. Similar to Optional, but with
 * an error object added for failure cases. Can conveniently deal with throwing functions without try-catch messes.
 */
@EqualsAndHashCode
public class Result<T, E extends Throwable> {
    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public <R> Result<R, ?> andThen(ThrowingFunction<T, Result<R, ?>> op) {
        if (isOk()) {
            return rethrowR(op.bind(value));
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    public <R> Result<R, ?> andThenTry(ThrowingFunction<T, R> op) {
        if (isOk()) {
            return trying(op.bind(value));
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    public <R> Result<R, ?> andThen(ThrowingSupplier<Result<R, ?>> op) {
        if (isOk()) {
            return rethrowR(op);
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    public Result<?, ?> andThenTry(ThrowingConsumer<T> op) {
        if (isOk()) {
            return trying(op.bind(value));
        } else {
            return this;
        }
    }

    public <R> Result<R, ?> andThen(ThrowingRunnable op) {
        if (isOk()) {
            rethrow(op);
            return ok();
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    public <R> Result<R, ?> andThenTry(ThrowingRunnable op) {
        if (isOk()) {
            return trying(op);
        } else {
            //noinspection unchecked
            return (Result<R, ?>) this;
        }
    }

    public Result<T, ?> orElse(T defaultValue) {
        if (isOk()) {
            return this;
        } else {
            return ok(defaultValue);
        }
    }

    public Result<T, ?> orElse(ThrowingFunction<E, Result<T, ?>> op) {
        if (isOk()) {
            return this;
        } else {
            return rethrowR(op.bind(error));
        }
    }

    public Result<T, ?> orElse(ThrowingSupplier<Result<T, ?>> op) {
        if (isOk()) {
            return this;
        } else {
            return rethrowR(op);
        }
    }

    public T get() throws E {
        if (isOk()) {
            return value;
        } else {
            throw error;
        }
    }

    public E getError() {
        return error;
    }

    public static <T> Result<T, ?> ok() {
        return new Result<>(null, null);
    }

    public static <T> Result<T, ?> trying(ThrowingSupplier<T> op) {
        try {
            return ok(op.get());
        } catch (Exception ex) {
            return err(ex);
        }
    }

    public static <T> Result<T, ?> trying(ThrowingRunnable op) {
        try {
            op.run();
            return ok();
        } catch (Exception ex) {
            return err(ex);
        }
    }

    public boolean isOk() {
        return error == null;
    }

    public static <T> Result<T, ?> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T, E extends Throwable> Result<T, E> err(E error) {
        return new Result<>(null, error);
    }
}
