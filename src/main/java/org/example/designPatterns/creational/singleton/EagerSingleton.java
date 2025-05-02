package org.example.designPatterns.creational.singleton;

//✅ Thread-safe by default.
//
//❌ Instance created even if never used.


public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() {} // private constructor

    public static EagerSingleton getInstance() {
        return instance;
    }
}

