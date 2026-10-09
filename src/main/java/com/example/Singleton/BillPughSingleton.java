package com.example.Singleton;

public class BillPughSingleton {

    private BillPughSingleton() {

    }

    // The JVM only loads Holder the first time getInstance() is called,
    // and class loading is thread-safe, so we get lazy + thread-safe with no locks
    private static class Holder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
