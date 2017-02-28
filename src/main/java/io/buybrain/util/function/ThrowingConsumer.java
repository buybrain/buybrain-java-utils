package io.buybrain.util.function;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;

    default ThrowingRunnable bind(T val) {
        return () -> this.accept(val);
    }
}