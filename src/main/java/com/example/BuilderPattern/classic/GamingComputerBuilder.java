package com.example.BuilderPattern.classic;

public class GamingComputerBuilder  implements Builder{
    private Computer computer = new Computer();

    public void buildCPU() {
        computer.setCpu("Gaming CPU");
    }

    public void buildRAM() {
        computer.setRam("16GB DDR4");
    }

    public void buildStorage() {
        computer.setStorage("1TB SSD");
    }

    public Computer getResult() {
        Computer result = computer;
        computer = new Computer();
        return result;
    }
}
