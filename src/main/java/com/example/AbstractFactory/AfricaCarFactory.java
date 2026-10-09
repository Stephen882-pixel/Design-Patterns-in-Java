package com.example.AbstractFactory;

// Added later: the client code in Main didn't have to change
public class AfricaCarFactory implements CarFactory {
    public Car createCar(){
        return new Pickup();
    }

    public CarSpecification createCarSpecification(){
        return new AfricaSpecification();
    }
}
