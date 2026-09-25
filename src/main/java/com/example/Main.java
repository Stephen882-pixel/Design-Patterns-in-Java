package com.example;


import com.example.Singleton.DoubleCheckingSingleton;
import com.example.Singleton.EagerSingleton;
import com.example.Singleton.LazySingleton;

public class Main {
    public static void main(String[] args) {
        System.out.println("Eager Singleton");
        EagerSingleton obj1 = EagerSingleton.getInstance();
        EagerSingleton obj2 = EagerSingleton.getInstance();
        System.out.println(obj1.hashCode());
        System.out.println(obj2.hashCode());
        System.out.println("===========================");
        System.out.println("Lazy Singleton");
        LazySingleton obj3 = LazySingleton.getInstance();
        LazySingleton obj4 = LazySingleton.getInstance();

        System.out.println(obj3.hashCode());
        System.out.println(obj4.hashCode());
        System.out.println("===========================");
        System.out.println("Double Checking Singleton");
        DoubleCheckingSingleton obj5 = DoubleCheckingSingleton.getInstance();
        DoubleCheckingSingleton obj6 = DoubleCheckingSingleton.getInstance();
        System.out.println(obj5.hashCode());
        System.out.println(obj6.hashCode());
    }
}