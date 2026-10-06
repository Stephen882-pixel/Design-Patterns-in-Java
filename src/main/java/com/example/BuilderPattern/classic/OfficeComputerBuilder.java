package com.example.BuilderPattern.classic;

public class OfficeComputerBuilder implements  Builder{
    private Computer computer = new Computer();

    public void buildCPU(){
        computer.setCpu("Intel Core i5 Office CPU");
    }

    public void buildRAM(){
        computer.setRam("8GB DDR4");
    }

    public void buildStorage(){
        computer.setStorage("512GB SSD");
    }

    public Computer getResult(){
        Computer result  = computer;
        computer = new Computer();
        return result;
    }
}
