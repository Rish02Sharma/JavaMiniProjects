package org.example.designPatterns.creational.singleton;

//✅ Thread-safe.
//
//❌ Slower due to synchronization on every call.

public class SynchronizedSingleton {
    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {}

    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }
}

