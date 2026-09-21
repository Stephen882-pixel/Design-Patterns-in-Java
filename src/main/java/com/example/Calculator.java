package com.example;

public class Calculator {

    int a;
    int b;

    private Calculator(){
        System.out.println("Calculator instance created");
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
