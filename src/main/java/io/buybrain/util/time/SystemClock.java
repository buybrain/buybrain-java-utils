package io.buybrain.util.time;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class SystemClock implements Clock {
    private static Clock INSTANCE = new SystemClock();

    private SystemClock() {
    }

    @Override
    @SneakyThrows
    public void sleep(@NonNull Duration duration) {
        TimeUnit.NANOSECONDS.sleep(duration.toNanos());
    }

    @Override
    public Instant now() {
        return Instant.now();
    }

    public static Clock get() {
        return INSTANCE;
    }
}
