package io.buybrain.util;

import lombok.val;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static io.buybrain.util.Exceptions.rethrow;
import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.testng.internal.collections.Ints.asList;

public class QueuedIteratorTest {
    @Test
    public void testBlocking() throws InterruptedException, ExecutionException {
        val SUT = new QueuedIterator<Integer>(3);

        val lastWritten = new AtomicInteger();

        // Spawn a thread that writes 5 elements to the iterator.
        val writer = newSingleThreadExecutor().submit(() -> {
            for (int i = 1; i <= 5; i++) {
                SUT.put(i);
                lastWritten.set(i);
            }
            SUT.done();
        });

        // Wait a little while to be sure the writer wrote 3 elements
        await().until(() -> lastWritten.get() == 3);
        // It should be blocking now. To be sure, wait a little while and check the last written value again
        sleep(100);
        assertThat(lastWritten.get(), is(3));

        // Now let's take a value and check that it could write the next number
        assertThat(SUT.hasNext(), is(true));
        assertThat(SUT.next(), is(1));
        await().until(() -> lastWritten.get() == 4);

        // Consume the rest of the iterator
        val remaining = new ArrayList<Integer>();
        SUT.forEachRemaining(remaining::add);

        assertThat(remaining, is(asList(2, 3, 4, 5)));
        writer.get();
    }

    @Test
    public void testDoneDoesNotBlock() {
        val SUT = new QueuedIterator<Integer>(3);
        SUT.put(1);
        SUT.put(2);
        SUT.put(3);
        SUT.done();

        // Consume the iterator
        val values = new ArrayList<Integer>();
        SUT.forEachRemaining(values::add);

        assertThat(values, is(asList(1, 2, 3)));
    }

    @Test
    public void testDoneWhileQueueEmpty() throws ExecutionException, InterruptedException {
        val SUT = new QueuedIterator<Integer>(3);

        val finisher = newSingleThreadExecutor().submit(() -> {
            rethrow(() -> sleep(100));
            SUT.done();
        });

        // Consume the iterator
        val values = new ArrayList<Integer>();
        SUT.forEachRemaining(values::add);

        assertThat(values, is(emptyList()));

        finisher.get();
    }
}
