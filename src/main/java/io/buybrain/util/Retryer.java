package io.buybrain.util;

import io.buybrain.util.function.ThrowingRunnable;
import io.buybrain.util.function.ThrowingSupplier;
import io.buybrain.util.time.Sleeper;
import io.buybrain.util.time.SystemClock;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Random;

/**
 * Utility for retrying a job until it succeeds. Supports policies such as maximum retries and exponential backoff.
 */
@Slf4j
public class Retryer {
    public static RunnableRetryer of(@NonNull ThrowingRunnable job) {
        return new RunnableRetryer(job);
    }

    public static <T> SupplierRetryer<T> of(@NonNull ThrowingSupplier<T> job) {
        return new SupplierRetryer<>(job);
    }

    private abstract static class BaseRetryer<T, R extends BaseRetryer> {
        private int maxAttempts = 0;
        private Duration baseDelay = Duration.ofSeconds(1);
        private boolean exponentialBackoff = true;
        private double exponentialFactor = 1.5;
        private boolean randomizeBackoff = false;
        private Long randomSeed = null;
        private Duration maxDelay = Duration.ofSeconds(30);
        private Sleeper sleeper = SystemClock.get();
        protected T returnValue;

        public R maxAttempts(int max) {
            maxAttempts = max;
            return (R) this;
        }

        public R baseDelay(@NonNull Duration delay) {
            baseDelay = delay;
            return (R) this;
        }

        public R exponentialBackoff(boolean exponential) {
            exponentialBackoff = exponential;
            return (R) this;
        }

        public R exponentialFactor(double factor) {
            exponentialFactor = factor;
            return (R) this;
        }

        public R randomizeBackoff() {
            randomizeBackoff = true;
            return (R) this;
        }

        public R randomizeBackoff(long seed) {
            randomSeed = seed;
            return randomizeBackoff();
        }

        public R maxDelay(@NonNull Duration delay) {
            maxDelay = delay;
            return (R) this;
        }

        public R clock(@NonNull Sleeper clock) {
            sleeper = clock;
            return (R) this;
        }

        protected void resolve() throws Exception {
            int attempts = 0;
            Exception lastException;
            Duration delay = baseDelay;
            Random random = randomSeed == null ? new Random() : new Random(randomSeed);

            while (true) {
                try {
                    returnValue = execute();
                    return;
                } catch (Exception ex) {
                    lastException = ex;
                }
                attempts++;
                if (maxAttempts > 0 && attempts == maxAttempts) {
                    throw lastException;
                }
                log.warn("CAUGHT EXCEPTION, WILL RETRY IN " + delay, lastException);

                sleeper.sleep(delay);
                if (exponentialBackoff) {
                    delay = applyExponentialBackoff(delay, random);
                }
            }
        }

        private Duration applyExponentialBackoff(Duration currentDelay, Random random) {
            double newDelayNanos = (double) currentDelay.toNanos() * exponentialFactor;
            if (randomizeBackoff) {
                // Scale the delay randomly between 50% and 150%
                newDelayNanos *= (random.nextDouble() + 0.5);
            }
            if (newDelayNanos <= maxDelay.toNanos()) {
                return Duration.ofNanos((long) newDelayNanos);
            } else {
                return maxDelay;
            }
        }

        protected abstract T execute() throws Exception;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RunnableRetryer extends BaseRetryer<Void, RunnableRetryer> {
        private final ThrowingRunnable job;

        public void run() throws Exception {
            resolve();
        }

        @Override
        protected Void execute() throws Exception {
            job.run();
            return null;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SupplierRetryer<T> extends BaseRetryer<T, SupplierRetryer<T>> {
        private final ThrowingSupplier<T> job;

        public T run() throws Exception {
            resolve();
            return returnValue;
        }

        @Override
        protected T execute() throws Exception {
            return job.get();
        }
    }
}
