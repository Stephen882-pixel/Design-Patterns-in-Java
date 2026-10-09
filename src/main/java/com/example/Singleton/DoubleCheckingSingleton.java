package com.example.Singleton;

public class DoubleCheckingSingleton {

    // volatile: makes sure other threads never see a half-built object
    private static volatile DoubleCheckingSingleton instance;

    private DoubleCheckingSingleton() {

    }

    public static DoubleCheckingSingleton getInstance() {
        if (instance == null) {                              // 1st check: no lock (fast path)
            synchronized (DoubleCheckingSingleton.class) {
                if (instance == null) {                      // 2nd check: with the lock
                    instance = new DoubleCheckingSingleton();
                }
            }
        }
        return instance;
    }
}
