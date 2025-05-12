package com.utc2.domainstore.utils;

import java.lang.ref.Cleaner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceTracker {
    private static final Cleaner cleaner = Cleaner.create();
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> instanceCounts = new ConcurrentHashMap<>();

    private static class CleanupTask implements Runnable {
        private final Class<?> clazz;

        public CleanupTask(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void run() {
            instanceCounts.get(clazz).decrementAndGet();
        }
    }

    public static Cleaner.Cleanable track(Object instance) {
        Class<?> clazz = instance.getClass();
        instanceCounts.computeIfAbsent(clazz, c -> new AtomicInteger()).incrementAndGet();
        return cleaner.register(instance, new CleanupTask(clazz));
    }

    public static int getLiveInstanceCount(Class<?> clazz) {
        AtomicInteger count = instanceCounts.get(clazz);
        return count == null ? 0 : count.get();
    }
}

