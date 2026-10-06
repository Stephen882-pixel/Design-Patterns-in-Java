package com.example.AbstractFactory;

public class NorthAmericaCarFactory implements CarFactory{
    public Car createCar(){
        return new Sedan();
    }

    public CarSpecification createCarSpecification(){
        return new NorthAmericaSpecification();
    }
}
