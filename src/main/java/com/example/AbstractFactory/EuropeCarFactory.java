package com.example.AbstractFactory;

public class EuropeCarFactory  implements CarFactory{
    public Car createCar(){
        return new Hatchback();
    }

    public CarSpecification createCarSpecification(){
        return new EuropeSpecification();
    }
}
