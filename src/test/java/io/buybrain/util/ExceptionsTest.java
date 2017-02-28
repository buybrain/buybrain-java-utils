package io.buybrain.util;

import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExceptionsTest {
    @Test
    public void testRethrowSupplier() {
        int res = Exceptions.rethrowR(() -> 42);
        assertThat(res, is(42));

        try {
            Exceptions.rethrowR(() -> {
                throw new Exception("42");
            });
        } catch (Exception ex) {
            assertThat(ex, instanceOf(RuntimeException.class));
            assertThat(ex.getCause().getMessage(), is("42"));
        }
    }

    @Test
    public void testRethrowFunction() {
        int res = Exceptions.<Integer, Integer>rethrowR(a -> a + 40).apply(2);
        assertThat(res, is(42));

        try {
            Exceptions.<Integer, Integer>rethrowR(a -> {
                throw new Exception(Integer.toString(a));
            }).apply(42);
        } catch (Exception ex) {
            assertThat(ex, instanceOf(RuntimeException.class));
            assertThat(ex.getCause().getMessage(), is("42"));
        }
    }

    @Test
    public void testRethrowConsumer() {
        AtomicInteger sidefx = new AtomicInteger();
        Exceptions.rethrow(sidefx::set).accept(42);

        assertThat(sidefx.get(), is(42));

        try {
            Exceptions.<Integer>rethrow(a -> {
                throw new Exception(Integer.toString(a));
            }).accept(42);
        } catch (Exception ex) {
            assertThat(ex, instanceOf(RuntimeException.class));
            assertThat(ex.getCause().getMessage(), is("42"));
        }
    }

    @Test
    public void testRethrowRunnable() {
        AtomicInteger sidefx = new AtomicInteger();
        Exceptions.rethrow(() -> sidefx.set(42));

        assertThat(sidefx.get(), is(42));

        try {
            Exceptions.rethrow(() -> {
                throw new Exception("42");
            });
        } catch (Exception ex) {
            assertThat(ex, instanceOf(RuntimeException.class));
            assertThat(ex.getCause().getMessage(), is("42"));
        }
    }
}
