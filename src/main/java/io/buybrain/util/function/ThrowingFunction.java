package io.buybrain.util.function;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Throwable;

    default ThrowingSupplier<R> bind(T val) {
        return () -> this.apply(val);
    }
}
