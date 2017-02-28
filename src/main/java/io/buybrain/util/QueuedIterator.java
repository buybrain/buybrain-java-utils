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
    private Elem next;

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
        queue.put(new Done());
    }

    @Override
    public boolean hasNext() {
        return !(fetchNext() instanceof Done);
    }

    @Override
    public T next() {
        val next = fetchNext();
        if (next instanceof Done) {
            throw new RuntimeException("Called next() on finished QueuedIterator");
        }
        this.next = null;
        //noinspection unchecked
        return ((Val) next).getElem();
    }

    @SneakyThrows
    private Elem fetchNext() {
        if (next == null) {
            next = queue.take();
        }
        return next;
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
