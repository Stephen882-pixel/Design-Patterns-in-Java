package com.example.Singleton;

// A naive lazy singleton: works with one thread, breaks with many (see Main section 6)
public class Calculator {

    int a;
    int b;

    private Calculator(){
        System.out.println("Calculator instance created");
        try {
            Thread.sleep(50); // pretend setup takes a moment, so the race is easy to see
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Calculator calc;

    public  static Calculator getInstance(){
        if(calc == null){
            calc = new Calculator();
        }
        return calc;
    }


    public int add(){
        return a + b;
    }
}
