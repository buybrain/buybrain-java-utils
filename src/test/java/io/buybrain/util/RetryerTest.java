package io.buybrain.util;

import io.buybrain.util.time.MockClock;
import lombok.val;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class RetryerTest {
    @Test
    public void testSimpleRunnable() throws Exception {
        val counter = new AtomicInteger();

        val clock = spy(new MockClock());

        Retryer.of(() -> {
            int tries = counter.incrementAndGet();
            if (tries <= 3) {
                throw new Exception("Failed (" + tries + ")");
            }
        }).clock(clock).run();

        assertThat(counter.get(), is(4));
    }

    @Test
    public void testSimpleSupplier() throws Exception {
        val counter = new AtomicInteger();

        val clock = spy(new MockClock());

        val result = Retryer.of(() -> {
            int tries = counter.incrementAndGet();
            if (tries <= 3) {
                throw new Exception("Failed (" + tries + ")");
            }
            return tries * 10;
        }).clock(clock).run();

        assertThat(result, is(40));
    }

    @Test
    public void testExponentialBackoff() throws Exception {
        val counter = new AtomicInteger();

        val clock = spy(new MockClock());

        Retryer.of(() -> {
            int tries = counter.incrementAndGet();
            if (tries <= 3) {
                throw new Exception("Failed (" + tries + ")");
            }
        })
            .clock(clock)
            .baseDelay(Duration.ofSeconds(2))
            .exponentialFactor(2.5)
            .maxDelay(Duration.ofSeconds(10))
            .run();

        // Check exponential backoff was applied correctly
        val ordered = inOrder(clock);
        ordered.verify(clock).sleep(Duration.ofMillis(2000));
        ordered.verify(clock).sleep(Duration.ofMillis(5000)); // x 2.5
        ordered.verify(clock).sleep(Duration.ofMillis(10000)); // x 2.5, Limited by max delay
        ordered.verifyNoMoreInteractions();
    }

    @Test
    public void testLinearDelay() throws Exception {
        val counter = new AtomicInteger();

        val clock = spy(new MockClock());

        Retryer.of(() -> {
            int tries = counter.incrementAndGet();
            if (tries <= 3) {
                throw new Exception("Failed (" + tries + ")");
            }
        })
            .clock(clock)
            .exponentialBackoff(false)
            .baseDelay(Duration.ofSeconds(5))
            .run();

        // Check it just repeat the base delay
        verify(clock, times(3)).sleep(Duration.ofMillis(5000));
        verifyNoMoreInteractions(clock);
    }

    @Test
    public void testMaxAttempts() throws Exception {
        val counter = new AtomicInteger();

        val retryer = Retryer.of(() -> {
            int tries = counter.incrementAndGet();
            throw new Exception("Failed (" + tries + ")");
        })
            .clock(new MockClock())
            .maxAttempts(2);

        try {
            retryer.run();
        } catch (Exception ex) {
            assertThat(ex.getMessage(), is("Failed (2)"));
        }
    }
}
