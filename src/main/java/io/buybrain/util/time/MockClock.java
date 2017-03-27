package io.buybrain.util.time;

import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;

public class MockClock implements Clock {
    private Instant time;

    public MockClock() {
        this(Instant.EPOCH);
    }

    public MockClock(@NonNull Instant time) {
        this.time = time;
    }

    public Instant now() {
        return time;
    }

    @Override
    public void sleep(@NonNull Duration duration) {
        time = time.plus(duration);
    }
}
