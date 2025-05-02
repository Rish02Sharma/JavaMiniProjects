package org.example.designPatterns.creational.singleton;

//❌ Not thread-safe.
//
//❗ Not recommended in multithreaded apps.

public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}

