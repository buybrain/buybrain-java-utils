package io.buybrain.util;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * An iterator backed by a queue that keeps producing values (or blocks) until it's explicitly ended.
 * Useful for iterating over collections that don't fit in memory.
 *
 * @param <T> the element type
 */
public class QueuedIterator<T> implements Iterator<T> {
    private final BlockingQueue<Elem> queue;
    private T next;
    private volatile boolean done = false;

    /**
     * QueuedIterator constructor
     *
     * @param capacity the maximum amount of queued elements before it starts blocking
     */
    public QueuedIterator(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Put the next value into this iterator. May block if the internal queue is at max capacity.
     *
     * @param elem the value
     */
    @SneakyThrows
    public void put(T elem) {
        queue.put(new Val(elem));
    }

    /**
     * Mark this iterator as done. It will publish all messages queued before this call and then end.
     */
    @SneakyThrows
    public void done() {
        done = true;
        // Try to put a Done message on the queue, but in a non-blocking manner.
        // We will check for done-ness after every received element, so adding this element is only in order to make
        // sure that the done gets picked up when the consumer is waiting for an empty queue.
        queue.offer(new Done());
    }

    @Override
    public boolean hasNext() {
        loadNext();
        return next != null;
    }

    @Override
    public T next() {
        loadNext();
        if (next == null) {
            throw new RuntimeException("Called next() on finished QueuedIterator");
        }
        val result = next;
        next = null;

        return result;
    }

    @SneakyThrows
    private void loadNext() {
        if (next == null && !(done && queue.isEmpty())) {
            val nextVal = queue.take();
            if (!(nextVal instanceof Done)) {
                //noinspection unchecked
                next = ((Val) nextVal).getElem();
            }
        }
    }

    private interface Elem {
    }

    @Value
    private class Val implements Elem {
        T elem;
    }

    private static class Done implements Elem {
    }
}
