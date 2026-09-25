package com.example.Singleton;

public class DoubleCheckingSingleton {

    private static  DoubleCheckingSingleton instance;

    public static  DoubleCheckingSingleton getInstance(){
        if(instance == null){
            synchronized (DoubleCheckingSingleton.class){
                if(instance == null){
                    instance = new DoubleCheckingSingleton();
                }
            }
        }
            return instance;
    }
}

