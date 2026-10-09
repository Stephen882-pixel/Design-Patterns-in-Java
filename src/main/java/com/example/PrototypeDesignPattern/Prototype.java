package com.example.PrototypeDesignPattern;

// Named Prototype (not Cloneable) so it doesn't clash with java.lang.Cloneable
public interface Prototype<T> {
    T customizeClone();
}
