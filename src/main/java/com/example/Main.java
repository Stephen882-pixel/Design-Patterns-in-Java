package com.example;


public class Main {
    public static void main(String[] args) {
        EagerSingleton obj1 = EagerSingleton.getInstance();
        EagerSingleton obj2 = EagerSingleton.getInstance();
        System.out.println(obj1.hashCode());
        System.out.println(obj2.hashCode());
        System.out.println("===========================");
        LazySingleton obj3 = LazySingleton.getInstance();
        LazySingleton obj4 = LazySingleton.getInstance();

        System.out.println(obj3.hashCode());
        System.out.println(obj4.hashCode());
    }
}