package com.example.Singleton;

// Java guarantees exactly one INSTANCE, even against reflection and serialization
public enum EnumSingleton {
    INSTANCE;

    private int counter = 0;

    public int nextTicket() {
        return ++counter;
    }
}
