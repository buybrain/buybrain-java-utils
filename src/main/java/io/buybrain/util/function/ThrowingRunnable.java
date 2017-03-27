package io.buybrain.util.function;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Throwable;
}
