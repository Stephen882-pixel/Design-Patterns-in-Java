package com.example.BuilderPattern.classic;

public class BudgetComputerBuilder implements Builder{
    private Computer computer = new Computer();

    public void buildCPU(){
        computer.setCpu("Intel Celeron");
    }

    public void buildRAM(){
        computer.setRam("4GB DDR3");
    }

    public void buildStorage(){
        computer.setStorage("256GB HDD");
    }

    public Computer getResult(){
        Computer result = computer;
        computer = new Computer();
        return result;
    }
}
